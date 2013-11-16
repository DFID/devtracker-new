package uk.gov.dfid.reporting

import uk.gov.dfid.reporting.DownloadStatuses.DownloadStatus

/**
 * Case class representing a single summary of an action taken on a resource
 * @param result The result as an instance of DownloadStatus
 * @param url The url of the resource
 * @param path The downloaded path of the resource
 * @param group The group the resource comes from
 * @param `package` The package name of the resource
 */
case class Summary(result: DownloadStatus, url: String, path: String, group: String, `package`: String, filetype: String)

