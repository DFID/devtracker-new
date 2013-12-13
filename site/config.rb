require "rubygems"
require "json"
require "helpers/formatters"
require "helpers/country_helpers"
require "helpers/region_helpers"
require "helpers/frontpage_helpers"
require "helpers/project_helpers"
require "helpers/codelists"
require "helpers/lookups"
require "helpers/sector_helpers"
require "middleman-smusher"
require 'httparty'

require "lib/api/country_api_client"
require "lib/api/countries_api_client"
require "lib/api/project_documents_api_client"

#------------------------------------------------------------------------------
# CONFIGURATION VARIABLES
#------------------------------------------------------------------------------

@api_root_url   = ENV['DFID_API_URL'] || "http://localhost:9000"
@api_access_url = "#{@api_root_url}/access"

#------------------------------------------------------------------------------
# IGNORE TEMPLATES AND PARTIALS
#------------------------------------------------------------------------------
ignore "/projectList.html"
ignore "/countries/country.html"
ignore "/countries/projects.html"
ignore "/countries/results.html"
ignore "/projects/summary.html"
ignore "/projects/documents.html"
ignore "/projects/transactions.html"
ignore "/projects/partners.html"
ignore "/sector/categories.html"
ignore "/sector/sectors.html"
ignore "/sector/projects.html"

#------------------------------------------------------------------------------
# NEW GENERATE COUNTRIES
#------------------------------------------------------------------------------



@Countries = CountriesApiClient.new(@api_root_url)
#@allProjBud = AllProjectsDocumentsApi.new(@api_root_url)
#@Countries.get_list_of_country_codes.each do |code|

	#@country_data = CountryApiClient.new(@api_root_url, code)
  	#proxy "/countries/#{country['code']}/index.html", "/countries/country.html",  :locals => { :data => @country_data }  
#end

@MyCountries = ["AF", "BD"]
@MyCountries.each do |code|
  @country_data = CountryApiClient.new(@api_root_url, code)
  proxy "/countries/#{@country_data.code}/index.html", "/countries/country.html",  :locals => { :country => @country_data }
end

#------------------------------------------------------------------------------
# DEFINE HELPERS - Import from modules to avoid bloat
#------------------------------------------------------------------------------
helpers do
  include Formatters
  include CountryHelpers
  include FrontPageHelpers
  include Lookups
  include ProjectHelpers
  include CodeLists
  include SectorHelpers
  include RegionHelpers
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

#------------------------------------------------------------------------------
# CONFIGURE DIRECTORIES
#------------------------------------------------------------------------------
set :css_dir   , 'stylesheets'
set :js_dir    , 'javascripts'
set :images_dir, 'images'

activate :livereload

#------------------------------------------------------------------------------
# BUILD SPECIFIC CONFIGURATION
#------------------------------------------------------------------------------
configure :build do
  activate :minify_css
  activate :minify_javascript
  activate :cache_buster
end