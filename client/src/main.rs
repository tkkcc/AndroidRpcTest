use std::{io::Read, time::Instant};

use base64::{
    prelude::{BASE64_STANDARD, BASE64_URL_SAFE},
    Engine,
};
use image::GenericImageView;
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug)]
struct Msg {
    data: String,
    width: u32,
    height: u32,
}
fn main() -> Result<(), Box<dyn std::error::Error>> {
    let start_time = Instant::now();
    let stream = reqwest::blocking::get("http://127.0.0.1:8080/screenshot")?.bytes()?;
    println!("response reseive");
    let msg: Msg = serde_json::from_slice(&stream)?;
    // println!("body: {}", body.data.len());
    println!("body: {:?} {}", msg.data.len(), msg.height);
    println!("start decode image");
    let img = BASE64_STANDARD.decode(msg.data)?;
    dbg!(21);
    // let img = image::load_from_memory(&img)?;
    // println!("img shape {}x{}", img.width(), img.height());

    println!("time {:?}", start_time.elapsed());
    Ok(())
}
