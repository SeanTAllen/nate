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

def todo_list
  template = Nate::Engine.from_file 'list.html'
  todos = ToDo.all( :complete => false )
  unless todos.empty?
    data = todos.collect do |todo|
      { '.title' => todo.title, 'input[name=id]' => { 'value' => todo.id }}
    end 
  else
    data = 'Nothing to do right now'
  end
  template.inject_with( { '.todo' => data } )
end

# controllers

get '/' do
  todo_list()
end

get '/new' do
  File.new 'form.html'
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