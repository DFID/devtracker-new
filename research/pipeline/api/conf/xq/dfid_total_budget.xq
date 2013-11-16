let $db      := db:open('iati')
let $budgets := for $budget in $db//iati-activity[reporting-org/@ref='GB-1']/budget
                let $date         := $budget/period-start/@iso-date
                let $current_date := current-date()
                let $month        := month-from-date($current_date)
                let $year         := if ($month < 4)
                                     then year-from-date($current_date) - 1
                                     else year-from-date($current_date)
                let $start_of_fy  := concat(string($year), "-04-01")
                let $end_of_fy    := concat(string($year+1), "-03-31")
                where $date >= $start_of_fy
                and   $date <= $end_of_fy
                return xs:int($budget/value)
return json:serialize(
    <json objects="json" numbers="budget">
        <dfidTotalBudget>{ sum($budgets) }</dfidTotalBudget>
    </json>)