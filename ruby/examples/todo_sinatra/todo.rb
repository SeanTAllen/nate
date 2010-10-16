require 'rubygems'
require 'sinatra'
require 'dm-core'
require 'dm-validations'
require 'dm-migrations'
require 'nate'

# Datamapper setup

DataMapper.setup( :default, "sqlite::memory:")

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

# helpers

def layout content
  layout = Nate::Engine.from_file 'layout.html'
  layout.inject_with( { '#content' => content } )
end

def todo_list
  template = Nate::Engine.from_file 'list.html'
  todos = ToDo.all( :complete => false )
  todo_data = todos.collect do |todo|
    { '.title' => todo.title, 'input[@name=id]' => { 'value' => todo.id }}
  end 
  data = { '.todo' => todo_data }
  template.inject_with( { '.todolist' => data } )
end

def form
  File.new( 'form.html', 'r' ).readlines.to_s
end

# controllers

get '/' do
  layout( todo_list() )
end

get '/new' do
  layout( form() )
end

post '/add' do
  ToDo.create( :title => params[:title], :created_at => Time.now )
  redirect '/'
end

post '/finished' do
  todo = ToDo.get( params[:id] )
  todo.update( :complete => true )
  redirect '/'
end