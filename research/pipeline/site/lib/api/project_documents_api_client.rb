require 'json'
require 'net/http'
require "open-uri"
require 'httparty'

class AllProjectsDocumentsApi

	def initialize(api_root_url)
		include HTTParty
		default_timeout 10000

		uri = URI("#{api_root_url}/api/page/projects/documents")

		res = Net::HTTP.get_response(uri)
		puts res.body 
		@@projectDocumentsJson = JSON.parse(res.body)


		@@allProjectsDocumentsObjects = @@projectDocumentsJson.map{|proj| 
			ProjectDocumentsApiClient.new(proj)
		}
	end 
end

class ProjectDocumentsApiClient

	@@projectDocumentsJson
 
	def initialize(proj)
		@@projectDocumentsJson = proj
		puts @@projectDocumentsJson
	end

	def iatiId
		@@projectDocumentsJson["iatiId"]
	end

	def title
		@@projectDocumentsJson["title"]
	end

	def participating_org
		@@projectDocumentsJson["participating-orgs"]
	end

	def recipient
		@@projectDocumentsJson["recipient"]
	end

	def documents
		@@documents
		puts @@projectDocumentsJson["documents"]
		@@documents = @@projectDocumentsJson["documents"].map { |doc|
		  Document.new(doc["title"], doc["url"], doc["format"], doc["categories"])
	  	}
		@@documents
	end
end

class Document

	@title
	@url
	@format
	@categories

	def initialize(title, url, format, categories)
		@title
		@url
		@format
		@categories
	end

	def title
		@title
	end

	def url
		@url
	end

	def format
		@format
	end

	def categories
		@categories
	end

end


