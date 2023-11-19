version 1.0

import "./hello.wdl" as sub

task start {
  command {
    echo "Starting...."
  }
  output {
    String out = read_string(stdout())
  }
}

task done {
  command {
    echo "Done"
  }
  output {
    String out = read_string(stdout())
  }
}

workflow main_workflow {
  call start
  call sub.hello as hello_english {
    input: greeting = "Hello"
  }
  call sub.hello as hello_spanish {
    input: greeting = "Hola"
  }
  call sub.hello as hello_french {
    input: greeting = "Bonjour"
  }
  call done
  output {
    Array[String] greetings = [hello_english.out, hello_spanish.out, hello_french.out]
  }
}