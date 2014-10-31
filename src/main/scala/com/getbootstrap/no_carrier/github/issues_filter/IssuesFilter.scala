package com.getbootstrap.no_carrier.github.issues_filter

sealed trait IssuesFilter {
    def codename: String
}
object AssignedToYou extends IssuesFilter {
    override val codename = "assigned"
}
object CreatedByYou extends IssuesFilter {
    override val codename = "created"
}
object MentioningYou extends IssuesFilter {
    override val codename =  "mentioned"
}
object SubscribedToByYou extends IssuesFilter {
    override val codename = "subscribed"
}
object All extends IssuesFilter {
    override val codename = "all"
}
