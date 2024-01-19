package cromwell.backend.impl.tes
import common.collections.EnhancedCollections._
import common.util.StringUtil._
import cromwell.backend.impl.tes.OutputMode.OutputMode
import cromwell.backend.{BackendConfigurationDescriptor, BackendJobDescriptor, BackendWorkflowDescriptor}
import cromwell.core.logging.JobLogger
import cromwell.core.path.{DefaultPathBuilder, Path}
import net.ceedubs.ficus.Ficus._

import scala.language.postfixOps
import scala.util.Try
import wdl.draft2.model.FullyQualifiedName
import wdl4s.parser.MemoryUnit
import wom.InstantiatedCommand
import wom.callable.Callable.OutputDefinition
import wom.expression.NoIoFunctionSet
import wom.values._

import scala.collection.immutable.Map

final case class WorkflowExecutionIdentityConfig(value: String) { override def toString: String = value.toString }
final case class WorkflowExecutionIdentityOption(value: String) { override def toString: String = value }
final case class TesTask(jobDescriptor: BackendJobDescriptor,
                         configurationDescriptor: BackendConfigurationDescriptor,
                         jobLogger: JobLogger,
                         tesPaths: TesJobPaths,
                         runtimeAttributes: TesRuntimeAttributes,
                         containerWorkDir: Path,
                         commandScriptContents: String,
                         instantiatedCommand: InstantiatedCommand,
                         dockerImageUsed: String,
                         mapCommandLineWomFile: WomFile => WomFile,
                         jobShell: String,
                         outputMode: OutputMode
                        ) {

  lazy val inputs: Seq[Input] = {
    val result =
      TesTask.buildTaskInputs(callInputFiles ++ writeFunctionFiles, workflowName, mapCommandLineWomFile) ++ Seq(
        commandScript
      )
    jobLogger.info(
      s"Calculated TES inputs (found ${result.size}): " + result.mkString(System.lineSeparator(),
        System.lineSeparator(),
        System.lineSeparator()
      )
    )
    result
  }
  // TODO add TES logs to standard outputs
  private lazy val standardOutputs = Seq("rc", "stdout", "stderr").map { f =>
    Output(
      name = Option(f),
      description = Option(fullyQualifiedTaskName + "." + f),
      url = Option(tesPaths.storageOutput(f)),
      path = tesPaths.containerOutput(containerWorkDir, f),
      `type` = Option("FILE")
    )
  }
  private lazy val cwdOutput = Output(
    name = Option("execution.dir.output"),
    description = Option(fullyQualifiedTaskName + "." + "execution.dir.output"),
    url = Option(tesPaths.callExecutionRoot.pathAsString),
    path = containerWorkDir.pathAsString,
    `type` = Option("DIRECTORY")
  )
  val name: String = fullyQualifiedTaskName
  val description: String = jobDescriptor.toString
  // TODO validate "project" field of workflowOptions
  val project =
    workflowDescriptor.workflowOptions.getOrElse("project", "")
  val outputs: Seq[Output] = {
    val result = outputMode match {
      case OutputMode.GRANULAR => standardOutputs ++ Seq(commandScriptOut) ++ womOutputs ++ additionalGlobOutput
      case OutputMode.ROOT => List(cwdOutput) ++ additionalGlobOutput
    }

    jobLogger.info(
      s"Calculated TES outputs (found ${result.size}): " + result.mkString(System.lineSeparator(),
        System.lineSeparator(),
        System.lineSeparator()
      )
    )

    result
  }
  val preferedWorkflowExecutionIdentity = TesTask.getPreferredWorkflowExecutionIdentity(
    workflowExecutionIdentityConfig,
    workflowExecutionIdentityOption
  )
  val executors = Seq(
    Executor(
      image = dockerImageUsed,
      command = Seq(jobShell, commandScript.path),
      workdir = runtimeAttributes.dockerWorkingDir,
      stdout = Option(tesPaths.containerOutput(containerWorkDir, "stdout")),
      stderr = Option(tesPaths.containerOutput(containerWorkDir, "stderr")),
      stdin = None,
      env = None
    )
  )
  val resources: Resources = TesTask.makeResources(
    runtimeAttributes,
    preferedWorkflowExecutionIdentity,
    Option(tesPaths.tesTaskRoot)
  )
  val tags: Map[String, Option[String]] = TesTask.makeTags(jobDescriptor.workflowDescriptor)
  private val workflowDescriptor = jobDescriptor.workflowDescriptor
  private val workflowName = workflowDescriptor.callable.name
  private val fullyQualifiedTaskName = jobDescriptor.taskCall.fullyQualifiedName

  // TODO extract output file variable names and match with Files below
  // The problem is that we only care about the files CREATED, so stdout and input redirects are ignored and
  // thus we can't directly match the names returned here to the files returned below. Also we have to consider Arrays
  //
  //  private val outputFileNames = jobDescriptor.call.task.outputs
  //    .filter(o => o.womType.toWdlString == "Array[File]" || o.womType.toWdlString == "File")
  //    .map(_.unqualifiedName)
  private val workflowExecutionIdentityConfig: Option[WorkflowExecutionIdentityConfig] =
    configurationDescriptor.backendConfig
      .getAs[String]("workflow-execution-identity")
      .map(WorkflowExecutionIdentityConfig)
  private val workflowExecutionIdentityOption: Option[WorkflowExecutionIdentityOption] =
    workflowDescriptor.workflowOptions
      .get(TesWorkflowOptionKeys.WorkflowExecutionIdentity)
      .toOption
      .map(WorkflowExecutionIdentityOption)
  // contains the script to be executed
  private val commandScript = Input(
    name = Option("commandScript"),
    description = Option(fullyQualifiedTaskName + ".commandScript"),
    url = Option(tesPaths.script.pathAsString),
    path = tesPaths.callExecutionDockerRoot.resolve("script").toString,
    `type` = Option("FILE"),
    content = None
  )
  private val commandScriptOut = Output(
    name = Option("commandScript"),
    description = Option(fullyQualifiedTaskName + ".commandScript"),
    url = Option(tesPaths.script.toString),
    path = tesPaths.callExecutionDockerRoot.resolve("script").toString,
    `type` = Option("FILE")
  )
  private val callInputFiles: Map[FullyQualifiedName, Seq[WomFile]] = jobDescriptor.fullyQualifiedInputs
    .safeMapValues {
      _.collectAsSeq { case w: WomFile => w }
    }
  // extract output files
  // if output paths are absolute we will ignore them here and assume they are redirects
  private val outputWomFiles: Seq[WomFile] = {
    import cats.syntax.validated._
    // TODO WOM: this should be pushed back into WOM.
    // It's also a mess, evaluateFiles returns an ErrorOr but can still throw. We might want to use an EitherT, although
    // if it fails we just want to fallback to an empty list anyway...
    def evaluateFiles(output: OutputDefinition): List[WomFile] =
      Try(
        output.expression
          .evaluateFiles(jobDescriptor.localInputs, NoIoFunctionSet, output.womType)
          .map(_.toList map { _.file })
      ).getOrElse(List.empty[WomFile].validNel)
        .getOrElse(List.empty)

    jobDescriptor.taskCall.callable.outputs
      .flatMap(evaluateFiles)
      .filter(o => !DefaultPathBuilder.get(o.valueString).isAbsolute)
  }
  private val womOutputs = outputWomFiles
    .flatMap(_.flattenFiles)
    .zipWithIndex
    .flatMap {
      case (f: WomSingleFile, index) =>
        val outputFile = f.value
        Seq(
          Output(
            name = Option(fullyQualifiedTaskName + ".output." + index),
            description = Option(fullyQualifiedTaskName + ".output." + index),
            url = Option(tesPaths.storageOutput(outputFile)),
            path = tesPaths.containerOutput(containerWorkDir, outputFile),
            `type` = Option("FILE")
          )
        )
      case (g: WomGlobFile, index) => handleGlobFile(g, index)
      case (d: WomUnlistedDirectory, index) =>
        val directoryPathName = "dirPath." + index
        val directoryPath = d.value.ensureSlashed
        val directoryListName = "dirList." + index
        val directoryList = d.value.ensureUnslashed + ".list"
        Seq(
          Output(
            name = Option(directoryPathName),
            description = Option(fullyQualifiedTaskName + "." + directoryPathName),
            url = Option(tesPaths.storageOutput(directoryPath)),
            path = tesPaths.containerOutput(containerWorkDir, directoryPath),
            `type` = Option("DIRECTORY")
          ),
          Output(
            name = Option(directoryListName),
            description = Option(fullyQualifiedTaskName + "." + directoryListName),
            url = Option(tesPaths.storageOutput(directoryList)),
            path = tesPaths.containerOutput(containerWorkDir, directoryList),
            `type` = Option("FILE")
          )
        )
    }
  private val additionalGlobOutput =
    jobDescriptor.taskCall.callable.additionalGlob.toList.flatMap(handleGlobFile(_, womOutputs.size))

  def handleGlobFile(g: WomGlobFile, index: Int) = {
    val globName = GlobFunctions.globName(g.value)
    val globDirName = "globDir." + index
    val globDirectory = globName + "/"
    val globListName = "globList." + index
    val globListFile = globName + ".list"
    Seq(
      Output(
        name = Option(globDirName),
        description = Option(fullyQualifiedTaskName + "." + globDirName),
        url = Option(tesPaths.storageOutput(globDirectory)),
        path = tesPaths.containerOutput(containerWorkDir, globDirectory),
        `type` = Option("DIRECTORY")
      ),
      Output(
        name = Option(globListName),
        description = Option(fullyQualifiedTaskName + "." + globListName),
        url = Option(tesPaths.storageOutput(globListFile)),
        path = tesPaths.containerOutput(containerWorkDir, globListFile),
        `type` = Option("FILE")
      )
    )
  }

  private def writeFunctionFiles: Map[FullyQualifiedName, Seq[WomFile]] =
    instantiatedCommand.createdFiles map { f => f.file.value.md5SumShort -> List(f.file) } toMap
}

object TesTask {
  // Helper to determine which source to use for a workflowExecutionIdentity
  def getPreferredWorkflowExecutionIdentity(configIdentity: Option[WorkflowExecutionIdentityConfig],
                                            workflowOptionsIdentity: Option[WorkflowExecutionIdentityOption]
                                           ): Option[String] =
    configIdentity.map(_.value).orElse(workflowOptionsIdentity.map(_.value))
  def makeResources(runtimeAttributes: TesRuntimeAttributes,
                    workflowExecutionId: Option[String],
                    internalPathPrefix: Option[String]
                   ): Resources = {
    /*
     * workflowExecutionId: This was added in BT-409 to let us pass information to an Azure
     * TES server about which user identity to run tasks as.
     * Note that we validate the type of WorkflowExecutionIdentity in TesInitializationActor.
     *
     * internalPathPrefix: Added in WX-1156 to support the azure TES implementation. Specifies
     * a working directory that the TES task can use.
     */
    val internalPathPrefixKey = "internal_path_prefix"
    val backendParameters: Map[String, Option[String]] = runtimeAttributes.backendParameters ++
      workflowExecutionId
        .map(TesWorkflowOptionKeys.WorkflowExecutionIdentity -> Option(_))
        .toMap ++
      internalPathPrefix
        .map(internalPathPrefixKey -> Option(_))
        .toMap
    val disk :: ram :: _ = Seq(runtimeAttributes.disk, runtimeAttributes.memory) map {
      case Some(x) =>
        Option(x.to(MemoryUnit.GB).amount)
      case None =>
        None
    }

    Resources(
      cpu_cores = runtimeAttributes.cpu.map(_.value),
      ram_gb = ram,
      disk_gb = disk,
      preemptible = Option(runtimeAttributes.preemptible),
      zones = None,
      backend_parameters = Option(backendParameters)
    )
  }

  def buildTaskInputs(taskFiles: Map[FullyQualifiedName, Seq[WomFile]],
                      workflowName: String,
                      womMapFn: WomFile => WomFile
                     ): List[Input] =
    taskFiles.flatMap { case (fullyQualifiedName, files) =>
      files.flatMap(_.flattenFiles).zipWithIndex.map { case (f, index) =>
        val inputType = f match {
          case _: WomUnlistedDirectory => "DIRECTORY"
          case _: WomSingleFile => "FILE"
          case _: WomGlobFile => "FILE"
        }
        Input(
          name = Option(fullyQualifiedName + "." + index),
          description = Option(workflowName + "." + fullyQualifiedName + "." + index),
          url = Option(f.value),
          path = womMapFn(f).value,
          `type` = Option(inputType),
          content = None
        )
      }
    }.toList

  def makeTags(workflowDescriptor: BackendWorkflowDescriptor): Map[String, Option[String]] = {
    // In addition to passing through any workflow labels, include relevant workflow ids as tags.
    val baseTags = workflowDescriptor.customLabels.asMap.map { case (k, v) => (k, Option(v)) }
    baseTags ++ Map(
      "workflow_id" -> Option(workflowDescriptor.id.toString),
      "root_workflow_id" -> Option(workflowDescriptor.rootWorkflowId.toString),
      "parent_workflow_id" -> workflowDescriptor.possibleParentWorkflowId.map(_.toString)
    )
  }

  def makeTask(tesTask: TesTask): Task =
    Task(
      id = None,
      state = None,
      name = Option(tesTask.name),
      description = Option(tesTask.description),
      inputs = Option(tesTask.inputs),
      outputs = Option(tesTask.outputs),
      resources = Option(tesTask.resources),
      executors = tesTask.executors,
      volumes = None,
      tags = Option(tesTask.tags),
      logs = None
    )
}

// Field requirements in classes below based off GA4GH schema
final case class Task(id: Option[String],
                      state: Option[String],
                      name: Option[String],
                      description: Option[String],
                      inputs: Option[Seq[Input]],
                      outputs: Option[Seq[Output]],
                      resources: Option[Resources],
                      executors: Seq[Executor],
                      volumes: Option[Seq[String]],
                      tags: Option[Map[String, Option[String]]],
                      logs: Option[Seq[TaskLog]]
                     )

final case class Executor(image: String,
                          command: Seq[String],
                          workdir: Option[String],
                          stdout: Option[String],
                          stderr: Option[String],
                          stdin: Option[String],
                          env: Option[Map[String, String]]
                         )

final case class Input(name: Option[String],
                       description: Option[String],
                       url: Option[String],
                       path: String,
                       `type`: Option[String],
                       content: Option[String]
                      ) {
  override def toString: String = {
    import common.util.StringUtil.EnhancedString

    // Mask SAS token signature in query
    this.getClass.getName + Seq(name, description, url.map(_.maskSensitiveUri), path, `type`, content)
      .mkString("(", ",", ")")
  }
}

final case class Output(name: Option[String],
                        description: Option[String],
                        url: Option[String],
                        path: String,
                        `type`: Option[String]
                       )

final case class Resources(cpu_cores: Option[Int],
                           ram_gb: Option[Double],
                           disk_gb: Option[Double],
                           preemptible: Option[Boolean],
                           zones: Option[Seq[String]],
                           backend_parameters: Option[Map[String, Option[String]]]
                          )

final case class OutputFileLog(url: String, path: String, size_bytes: Int)

final case class TaskLog(start_time: Option[String],
                         end_time: Option[String],
                         metadata: Option[Map[String, String]],
                         logs: Option[Seq[ExecutorLog]],
                         outputs: Option[Seq[OutputFileLog]],
                         system_logs: Option[Seq[String]]
                        )

final case class ExecutorLog(start_time: Option[String],
                             end_time: Option[String],
                             stdout: Option[String],
                             stderr: Option[String],
                             exit_code: Option[Int]
                            )