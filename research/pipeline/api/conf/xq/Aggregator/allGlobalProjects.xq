 START n=node:entities(type="iati-activity")
        | MATCH n-[:`related-activity`]-p
        | WHERE n.hierarchy! = 2
        | AND  (n.`recipient-region`! = "Administrative/Capital (AC)"
        |    OR n.`recipient-region`! = "Non Specific Country (NS)"
        |    OR n.`recipient-region`! = "Multilateral Organisation (ZZ)")
        | AND   p.type=1
        | RETURN DISTINCT(p.ref) as id, n.`recipient-region`! as region

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
let $regional-projects 	:= for $component in $db//iati-activity[@hierarchy!="2"][recipient-region]
							let 	$recipient-region := $component/recipient-region
							where 	$recipient-region != "Administrative/Capital (AC)"
							or 		$recipient-region != "Non Specific Country (NS)"
							or 		$recipient-region != "Multilateral Organisation (ZZ)"
							return $component
return  $regional-projects                    