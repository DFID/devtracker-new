require 'json'
require 'net/http'
require "open-uri"
require 'httparty'

class CountryApiClient
	include HTTParty
	default_timeout 10000

	@@countryJson

	def initialize(api_root_url, countryCode)

		uri = URI("#{api_root_url}/api/page/country/#{countryCode}")

		res = Net::HTTP.get_response(uri)
		puts res.body 
		@@countryJson = JSON.parse(res.body)

		#bd = File.read('lib/api/BD.json')	
		#@@countryJson = JSON.parse(bd.to_s)
	end

	def code
		@@countryJson["code"]
	end

	def name
		@@countryJson["name"]
	end

	def description
		@@countryJson["description"]
	end

	def resultsCount
		@@countryJson["resultsCount"]
	end

	def total_budget
		@@totBudget
		@@countryJson["stats"].each do |stat|
		  @@totBudget = stat["totalBudget"]
		end
		@@totBudget.to_f

	end

	def population
		@@pop
		@@countryJson["stats"].each do |stat|
			@@pop = stat["population"] 
		end
		@@pop
	end

	def below_poverty_line
		@@belowPovertyLine
		@@countryJson["stats"].each do |stat|
			@@belowPovertyLine = stat["belowPovertyLine"]
		end
		@@belowPovertyLine
	end

	def fertility_rate
		@@fertilityRate
		@@countryJson["stats"].each do |stat|
			@@fertilityRate = stat["fertilityRate"]
		end
		@@fertilityRate
	end

	def gdp_growth_rate
		@@gdpGrowthRate
		@@countryJson["stats"].each do |stat|
			@@gdpGrowthRate = stat["gdpGrowthRate"]
		end
		@@gdpGrowthRate
	end

	def sector_hierarchies
		sectorHier = []
		@@countryJson["SectorBreakdown"].each do |sec|
			sectorHier << SectorObject.new(sec["sector"], sec["percentage"]) 
		end
		uniqueSectors = sectorHier.uniq{|s| s.sector}

		totalVisableSectors = uniqueSectors.first(5).map{|x| x.percentage}.reduce(:+)

		sectorsForDisplay = uniqueSectors.first(5) << SectorObject.new("Others", 100 - totalVisableSectors)
		sectorsForDisplay.map{ |s| SectorObject.new(s.sector, s.percentage.round(2).to_s + "%") }
	end

	def sector_hierarchies_json
		@@countryJson["SectorBreakdown"].to_json
	end

	def locations_json
		@@countryJson["locations"].to_json
	end

	def project_budgets_by_year_json
		
		@budByYear = [["Year", "Budget"]]
		@sortedBudgets = @@countryJson["ProjectBudgetsByYear"].sort_by  { |pb| pb["label"] }.last(6)

		@sortedBudgets.each do |budget|
			@budByYear << [budget["label"],budget["value"]]
		end
		@budByYear
	end

	def dfid_total_budget
		@@countryJson["dfidTotalBudget"].to_f
	end

	def active_projects		
		@@countryJson["projects"]["active"]
	end
end

class SectorObject

	@sector
	@percentage

	def initialize(sector, percentage)
		@sector = sector
		@percentage = percentage
	end

	def sector
		@sector
	end

	def percentage
		@percentage
	end
end

module Net
    class HTTP
        alias old_initialize initialize

        def initialize(*args)
            old_initialize(*args)
            @read_timeout = 60*60     # 5 minutes
        end
    end
end


#sets the timeout for the http requests

#puts CountryApiClient.new("http://localhost:9000","BD")
#puts CountryApiClient.new("http://localhost:9000","BD").code
