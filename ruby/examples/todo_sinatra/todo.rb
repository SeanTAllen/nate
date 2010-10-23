require 'rubygems'
require 'sinatra'
require 'dm-core'
require 'dm-validations'
require 'dm-migrations'
require 'sanitize'
require 'nate'

# Datamapper setup

db_temp_file = Tempfile.new( "nate_todo_db" )
DataMapper.setup( :default, "sqlite://#{db_temp_file.path}")

class ToDo
  include DataMapper::Resource

  property :id,         Serial
  property :title,      String
  property :created_at, DateTime
  property :complete,   Boolean, :default=>false

  validates_presence_of :title
end

DataMapper.finalize
DataMapper.auto_migrate!

# templates

Layout = Nate::Engine.from_file 'templates/layout.html'
List   = Nate::Engine.from_file 'templates/list.html'

# helpers

def layout content
  Layout.inject_with( { '#content' => content } )
end

def todo_list
  template =
  todos = ToDo.all( :complete => false )
  todo_data = todos.collect do |todo|
    { '.title' => todo.title,  'input[@name=id]' => { 'value' => todo.id }}
  end 
  data = { '.todo' => todo_data }
  List.inject_with( { '.todolist' => data } )
end

def form
  File.new( 'templates/form.html', 'r' ).readlines.to_s
end

# controllers

get '/' do
  layout( todo_list() )
end

get '/new' do
  layout( form() )
end

post '/add' do
  ToDo.create( :title => Sanitize.clean( params[:title] ), :created_at => Time.now )
  redirect '/'
end

post '/finished' do
  todo = ToDo.get( params[:id] )
  todo.update( :complete => true )
  redirect '/'
end