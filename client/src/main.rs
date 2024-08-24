use std::{io::Read, time::Instant};

use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug)]
struct Msg {
    data: Vec<u8>,
    width: u32,
    height: u32,
}
fn main() -> Result<(), Box<dyn std::error::Error>> {
    let start_time = Instant::now();
    let stream = reqwest::blocking::get("http://127.0.0.1:8080/screenshot")?;
    let msg: Msg = serde_json::from_reader(stream)?;
    // println!("body: {}", body.data.len());
    println!("body: {:?}", msg.data.len());
    println!("time {:?}", start_time.elapsed());
    Ok(())
}
