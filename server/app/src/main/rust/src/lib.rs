use core::panic;
use std::{
    fs,
    io::Read,
    net::{IpAddr, Ipv6Addr},
    sync::LazyLock,
};

use image::{codecs::png::PngEncoder, DynamicImage, GenericImageView, ImageEncoder};
use jni::{
    objects::{JClass, JObject},
    JNIEnv,
};
use log::error;
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize, Debug)]
struct Msg {
    #[serde(with = "serde_bytes")]
    data: Vec<u8>,
}

use futures::{io::BufWriter, prelude::*};
use tarpc::{
    context,
    server::{self, incoming::Incoming, Channel},
    tokio_serde::formats::{Bincode, Json},
};

// This is the service definition. It looks a lot like a trait definition.
// It defines one RPC, hello, which takes one arg, name, and returns a String.
#[tarpc::service]
trait World {
    /// Returns a greeting for name.
    async fn start() -> Msg;
}

#[derive(Clone)]
struct HelloServer;

impl World for HelloServer {
    #[doc = " Returns a greeting for name."]
    async fn start(self, context: ::tarpc::context::Context) -> Msg {
        static DATA: LazyLock<DynamicImage> = LazyLock::new(|| {
            error!("start data allocation");
            // let data = vec![255u8; 2880 * 1620 * 2];
            let data = image::ImageReader::open("/tmp/1.jpg")
                .unwrap()
                .decode()
                .unwrap();

            error!("47");
            let data = data.resize_exact(2880, 1620, image::imageops::FilterType::Triangle);
            data.into_rgba8().into()
        });
        let data = &DATA;

        // error!("48");
        // let mut buf = Vec::new();
        // error!("49");
        // let encoder = PngEncoder::new(&mut buf);
        // error!("50 {}x{}", data.width(), data.height());
        // let e = encoder.write_image(
        //     data.as_bytes(),
        //     data.width(),
        //     data.height(),
        //     data.color().into(),
        // );
        // error!("51 {:?}", e);

        let buf = data.as_bytes();

        error!("end  data allocation");
        // let Ok(buf) = buf else {
        //     error!("{:?}", buf);
        //     panic!();
        // };
        error!("success");
        Msg { data: buf.into() }
    }
}

#[tokio::main]
async fn main() -> anyhow::Result<()> {
    let server_addr = (IpAddr::V6(Ipv6Addr::LOCALHOST), 8081);
    error!("start server {:?}", &server_addr);

    let sock_path = "/data/data/com.example.androidrpctest/cache/a.sock";
    fs::remove_file(&sock_path);
    let mut listener = tarpc::serde_transport::tcp::listen(&server_addr, Bincode::default).await?;
    // let mut listener = tarpc::serde_transport::unix::listen(&sock_path, Bincode::default).await?;
    listener.config_mut().max_frame_length(usize::MAX);
    listener
        // Ignore accept errors.
        .filter_map(|r| future::ready(r.ok()))
        .map(server::BaseChannel::with_defaults)
        // Limit channels to 1 per IP.
        // .max_channels_per_key(1, |t| t.transport().peer_addr().unwrap().ip())
        // serve is generated by the service attribute. It takes as input any type implementing
        // the generated World trait.
        .map(|channel| {
            let server = HelloServer {};
            channel.execute(server.serve()).for_each(|x| async {
                tokio::spawn(x);
            })
        })
        // Max 10 channels.
        .buffer_unordered(10)
        .for_each(|_| async {})
        .await;

    // let (client_transport, server_transport) = tarpc::transport::channel::unbounded();

    // let server = server::BaseChannel::with_defaults(server_transport);

    // // WorldClient is generated by the #[tarpc::service] attribute. It has a constructor `new`
    // // that takes a config and any Transport as input.
    // let mut client = WorldClient::new(client::Config::default(), client_transport).spawn();
    //
    // // The client has an RPC method for each RPC defined in the annotated trait. It takes the same
    // // args as defined, with the addition of a Context, which is always the first arg. The Context
    // // specifies a deadline and trace information which can be helpful in debugging requests.
    // let hello = client.start(context::current()).await?;

    // println!("{hello:?}");

    Ok(())
}

#[no_mangle]
extern "C" fn Java_com_example_androidrpctest_Native_start(
    mut env: JNIEnv,
    class: JClass,
    host: JObject,
) {
    android_logger::init_once(
        android_logger::Config::default().with_max_level(log::LevelFilter::Debug),
    );
    let res = main();
    error!("{res:?}");
}
