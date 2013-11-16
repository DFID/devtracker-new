START n=node:entities(type="iati-activity")
        | MATCH n-[r?:`recipient-region`]-a,
        |       n-[:`related-activity`]-p
        | WHERE n.hierarchy! = 2
        | // Parent Activity must have a
        | AND   p.type=1
        | AND   (
        |       (r is not null)
        |   OR  (
        |         (r is null)
        |     AND (
        |          n.`recipient-region`! = "Balkan Regional (BL)"
        |       OR n.`recipient-region`! = "East Africa (EA)"
        |       OR n.`recipient-region`! = "Indian Ocean Asia Regional (IB)"
        |       OR n.`recipient-region`! = "Latin America Regional (LE)"
        |       OR n.`recipient-region`! = "East African Community (EB)"
        |       OR n.`recipient-region`! = "EECAD Regional (EF)"
        |       OR n.`recipient-region`! = "East Europe Regional (ED)"
        |       OR n.`recipient-region`! = "Francophone Africa (FA)"
        |       OR n.`recipient-region`! = "Central Africa Regional (CP)"
        |       OR n.`recipient-region`! = "Overseas Territories (OT)"
        |       OR n.`recipient-region`! = "South East Asia (SQ)"
        |     )
        |   )
        | )
        | RETURN DISTINCT(p.ref) as id, a.code as code, n.`recipient-region`? as region

        let $db        := db:open('iati')
let $countries := for $country in $db//iati-activity[reporting-org/@ref='GB-1']/recipient-country
                  let $code := $country/@code
                  let $name := $country/text()
                  group by $code
                  return <country>
                           <code>{ $code }</code>
                           <name>{ $name[1] }</name>
                         </country>
return json:serialize(
   <json arrays="json" objects="country">
     { $countries }
   </json>)


let $db                                       :=  db:open('iati')
let $iatiProjects               :=  for $iatiProject in $db//iati-activity
                          let $id           :=  $iatiProject/iati-identifier//text()
                          let $status       :=  $iatiProject/activity-status/data(@code)
                          let $title        :=  $iatiProject/title//text()
                          let $description  :=  $iatiProject/description//text()
                          let $projectOrgs  :=  $iatiProject/participating-org
                          let $projectType  :=  switch ($id) 
                                                   case "Cow" return "Moo"
                                                   case "Cat" return "Meow"
                                                   case "Duck" return "Quack"
                                                   default return "What's that odd noise?" 

   
------------------------------------------------------------------------
   let $db                                       :=  db:open('iati')

   let $regionalProjects := for $component in $db//iati-activity
                                let $regionCount        := count($component//recipient-region)
                                let $regionName         := $component//recipient-region//text()
                                
                                where ($regionCount >0)
                                   or  (($regionCount =0)
                                     and (
                                        let $associatedRegions  := for $associatedActivity in $component/related-activity[data(@ref)]
                                                                let  $associatedRegion := $component[iati-identifier=$associatedActivity]/recipient-region//text()
                                        
                                          $regionName != "Balkan Regional (BL)"
                                       or $regionName != "East Africa (EA)"
                                       or $regionName != "Indian Ocean Asia Regional (IB)"
                                       or $regionName != "Latin America Regional (LE)"
                                       or $regionName != "East African Community (EB)"
                                       or $regionName != "EECAD Regional (EF)"
                                       or $regionName != "East Europe Regional (ED)"
                                       or $regionName != "Francophone Africa (FA)"
                                       or $regionName != "Central Africa Regional (CP)"
                                       or $regionName != "Overseas Territories (OT)"
                                       or $regionName != "South East Asia (SQ)"
                                     )
                                   )
                                 