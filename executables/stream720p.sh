#!/bin/bash
cd /home/nathan/bin
./ffmpeg -y -f v4l2 -s 1280x720 -r 30 -i /dev/video0 -pix_fmt yuv420p -profile:v baseline -level 3.0 -c:v libx264 -tune zerolatency -preset ultrafast -x264opts crf=28 -x264opts keyint=100:min-keyint=20  -f mpegts udp://137.194.23.104:1234

