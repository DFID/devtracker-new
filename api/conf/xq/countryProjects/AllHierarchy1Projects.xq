
let $db      := db:open('iati')

let $projects := for $project in $db//iati-activity[reporting-org/@ref='GB-1'][@hierarchy=1]
                              let $documents := <document><url></url><category></category></document>
                              return <project>
                                        <status>{data($project//activity-status)}</status>
                                        <title>{data($project//title)}</title>
                                        <description>{data($project//description)}</description>
                                        <iati-id>{data($project//iati-identifier)}</iati-id>
                                        <start-actual>{data($project//activity-date[@type="start-actual"])}</start-actual>
                                        <start-planned>{data($project//activity-date[@type="start-planned"])}</start-planned>
                                        <end-actual>{data($project//activity-date[@type="end-actual"])}</end-actual>
                                        <end-planned>{data($project//activity-date[@type="end-planned"])}</end-planned>
                                    </project>  
                         
return json:serialize(
            <json arrays="json" objects="project" strings="status title description iati-id start-planned start-actual end-planned end-actual" >
              {$projects} 
            </json> )          
          