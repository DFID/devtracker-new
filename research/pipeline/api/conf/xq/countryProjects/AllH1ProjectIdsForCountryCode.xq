(: A query to get all the projects for a specified country :)

declare variable $country_code := 'BD';

let $db      := db:open('iati')
let $allCountryProjectIds := for $projectId in distinct-values($db//iati-activity[recipient-country/@code=$country_code][reporting-org/@ref='GB-1']//related-activity[@type=1]/data(@ref))
                return <id>{$projectId}</id>

return json:serialize(
            <json arrays="json" strings="id" >
              {$allCountryProjectIds} 
            </json> )   
