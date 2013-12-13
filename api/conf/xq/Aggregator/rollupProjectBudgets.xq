| START  txn = node:entities(type="transaction")
        | MATCH  project-[:`related-activity`]-component-[:transaction]-txn,
        |        component-[:`reporting-org`]-org,
        |        txn-[:value]-value,
        |        txn-[:`transaction-date`]-date,
        |        txn-[:`transaction-type`]-type
        | WHERE  project.type = 1
        | AND    org.ref      = "GB-1"
        | AND    (type.`code` = 'D' OR type.`code` = 'E')
        | RETURN
        | distinct project.ref as id,
        | sum(value.value)     as spend
        