use i_face::backend::Proc;
use i_face::frontend::Front;
use std::env;
use std::error::Error;
use std::process::{Command, Stdio};
use std::thread;
use std::time::Duration;

fn main() {
    run().expect("Error!!!");
}

fn run() -> Result<(), Box<Error>> {
    let child = if !is_dev() {
        Command::new("java")
            .arg("-jar")
            .arg("simqlToCsv-assembly-0.1.0-SNAPSHOT.jar")
            .stdin(Stdio::piped())
            .spawn()?
    } else {
        Command::new("bash")
            .arg("dev.sh")
            .stdin(Stdio::piped())
            .spawn()?
    };
    let mut p = Proc::new(child);
    let mut f = Front::new();
    let mut c = |s: String| -> () {
        let ns = format!("{}{}", s, "\n");
        p.send(ns.as_bytes());
    };
    f.read_loop(&mut c);
    thread::sleep(Duration::from_secs(1));
    p.kill()?;
    Ok(())
}

fn is_dev() -> bool {
    let appdir = env::args()
        .collect::<Vec<String>>()
        .pop()
        .expect("can't get app name.");
    appdir.contains("debug")
}
