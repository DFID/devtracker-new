declare variable $country_code external;
let $db        := db:open('iati')

let $regionalProjects := for $regionalProject in $db//iati-activity[reporting-org/@ref='GB-1'][recipient-country/@code=$country_code]
                          let $theRegionalProjects := $regionalProject
                          where $regionalProject//related-activity/@type = 1
                          and ( exists($regionalProject//recipient-region)
                          or( empty($regionalProject//recipient-region) 
                            and(
                              $regionalProject//recipient-region    = "Balkan Regional (BL)"
                              or $regionalProject//recipient-region = "East Africa (EA)"
                              or $regionalProject//recipient-region = "Indian Ocean Asia Regional (IB)"
                              or $regionalProject//recipient-region = "Latin America Regional (LE)"
                              or $regionalProject//recipient-region = "East African Community (EB)"
                              or $regionalProject//recipient-region = "EECAD Regional (EF)"
                              or $regionalProject//recipient-region = "East Europe Regional (ED)"
                              or $regionalProject//recipient-region = "Francophone Africa (FA)"
                              or $regionalProject//recipient-region = "Central Africa Regional (CP)"
                              or $regionalProject//recipient-region = "Overseas Territories (OT)"
                              or $regionalProject//recipient-region = "South East Asia (SQ)"
                            )
                          )            
                        )
                  return $theRegionalProjects
return $regionalProjects
