---
layout: post
title: "Install Octave on Ubuntu"
description: "在ubuntu中安装Octave"
category: machine-learning 
tags: [octave]
---

安装octave前，先运行<code>sudo apt-get build-dep octave</code>安装所需依赖包

然后运行<code>sudo apt-get install octave</code>

如果需要安装最新版本的可以下载源文件之后，通过以下命令安装
<pre><code>
sudo apt-get build-dep octave
wget ftp://ftp.gnu.org/gnu/octave/octave-4.0.0.tar.gz
tar xf octave-4.0.0.tar.gz
cd octave-4.0.0/
./configure
make 
sudo make install
</code></pre> 
