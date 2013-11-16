require 'httparty'
require 'json'

class CountriesApiClient
	@@countriesJson
	def initialize(api_root_url)
		@api_root_url = api_root_url
		response = HTTParty.get("#{@api_root_url}/api/countries")
    	@@countriesJson = JSON.parse(response.body)
	end

	def get_list_of_country_codes
    	codes = @@countriesJson.map { |e| e["code"] }
	end

end
#puts CountriesApiClient.new("http://localhost:9000")
#puts CountriesApiClient.new("http://localhost:9000").get_list_of_country_codes

