# "Github Custom Unstable Build Commit Status SCM Behaviour" Jenkins plugin

This plugin allows to customize commit status for unstable builds. By default `github-branch-source-plugin` [marks unstable builds' commit status as failed](https://github.com/jenkinsci/github-branch-source-plugin/blob/923184ce70fc84252937b8d08872f666efcc288b/src/main/java/org/jenkinsci/plugins/github_branch_source/GitHubNotificationContext.java#L230-L232) making it impossible to have test reporting enabled for failing tests aka unstable builds leaving only two options:
- Fix the builds -- might not always be possible / feasible / priority.
- Do not report -- leads to loss of useful test metrics over time, and reporting can help prevent and implement the minimal "do not break anything new" rule.

This was implemented by Github Branch Source trait.

## How to use this plugin

After installing go to the job configuration. Under "Branch sources" -> "GitHub" -> "Behaviors" click "Add" and select "Custom Github Commit Status For Unstable Builds" from the dropdown menu. Then you can select the checkbox if you want to mark unstable builds' commit statuses as success, or deselect it for default behavior.
