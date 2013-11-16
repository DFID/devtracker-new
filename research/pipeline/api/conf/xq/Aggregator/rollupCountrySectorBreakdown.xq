 START n=node:entities(type="iati-activity")
        | MATCH n-[:`recipient-country`]-c,
        |       n-[:`reporting-org`]-o,
        |       n-[:sector]-s
        | WHERE n.hierarchy! = 2
        | AND   o.ref = "GB-1"
        | RETURN distinct c.code as country, s.code as sector, s.sector as name, COUNT(s) as total
        | ORDER BY total DESC



        
let $db                 := db:open('iati')
let $projects := for $component in $db//iati-activity[@hierarchy!="2"][reporting-org/@ref='GB-1'][recipient-country]
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



---------------------------------------------------------------

let $db                 := db:open('iati')
let $projects := for $component in $db//iati-activity[reporting-org][recipient-country][sector]
                    for $uniqueCode in $component/recipient-country/data(@code)
                      group by $uniqueCode
                      return  for $countrySector in $component[recipient-country/@code=$uniqueCode]/sector
                                let $sector := $component/data(@code)
                                let $name     := $component//text()
                                group by $countrySector
                                return    <rollupCountrySectorBreakdown>
                                            <country>{$uniqueCode}</country>
                                            <sector>{$sector}</sector>
                                            <name>{$name}</name>
                                            <total-revenue>{count($sector)}</total-revenue>
                                          </rollupCountrySectorBreakdown>
                                   
 return <stuff>{$projects}</stuff>
 
 
                 