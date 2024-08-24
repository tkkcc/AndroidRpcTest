#!/usr/bin/env bash

run() {
  set -e
  cargo ndk -t x86 build --release
  adb push target/i686-linux-android/release/client /data/local/tmp/
  adb shell /data/local/tmp/client
}
dev() {
  cargo watch -w src -s -- ./0.sh run
}

"$@"
