#!/usr/bin/env ruby

require 'webrick'
include WEBrick

server = HTTPServer.new( :Port => 4567, :DocumentRoot => File.join(Dir.pwd, "/public") )

trap( 'INT' ) { server.shutdown }

server.start
