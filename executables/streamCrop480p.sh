#!/bin/bash
cd /home/mint/bin
./ffmpeg -y -f v4l2 -s 1280x720 -r 30 -i /dev/video0 -pix_fmt yuv420p -profile:v baseline -level 3.0 -c:v libx264 -tune zerolatency -preset ultrafast -x264opts crf=28 -x264opts keyint=100:min-keyint=20 -filter:v "crop=480:240:0:0" -f mpegts udp://192.168.0.37:1234
