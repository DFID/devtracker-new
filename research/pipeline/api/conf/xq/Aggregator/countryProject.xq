declare variable $country_code external;

let $db                 := db:open('iati')
let $projects := for $component in $db//iati-activity[@hierarchy!="2"][reporting-org/@ref='GB-1'][recipient-country][recipient-country=$country_code][related-activity/@type=1]
                 let $project := data($component/related-activity[@type=1]/@ref[1])
                 let $country := data($component/recipient-country/@code[1])
                 return <project>
                          <id>{ $project }</id>
                          <country>{ $country }</country>
                        </project>
return json:serialize( 
        <json arrays="projects" objects="json project" strings="id country">
          <projects>
            { $projects }
          </projects>
        </json>
        )