package org.jenkinsci.plugins.githubScmTraitNotificationContextUnstableCommitStatus;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.trait.SCMBuilder;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceTrait;
import jenkins.scm.api.trait.SCMSourceTraitDescriptor;
import org.jenkinsci.plugins.github_branch_source.*;
import org.kohsuke.github.GHCommitState;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class NotificationContextTrait extends SCMSourceTrait {
    private boolean shouldUnstableBeMarkedAsSuccess;

    @DataBoundConstructor
    public NotificationContextTrait(boolean shouldUnstableBeMarkedAsSuccess) {
        this.shouldUnstableBeMarkedAsSuccess = shouldUnstableBeMarkedAsSuccess;
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        GitHubSCMSourceContext githubContext = (GitHubSCMSourceContext) context;
        githubContext.withNotificationStrategies(Collections.singletonList(
                new CustomContextNotificationStrategy(shouldUnstableBeMarkedAsSuccess)));
    }

    @Extension
    public static class DescriptorImpl extends SCMSourceTraitDescriptor {

        @NonNull
        @Override
        public String getDisplayName() {
            return "Custom Github Commit Status For Unstable Builds";
        }

        @Override
        public Class<? extends SCMBuilder> getBuilderClass() {
            return GitHubSCMBuilder.class;
        }

        @Override
        public Class<? extends SCMSourceContext> getContextClass() {
            return GitHubSCMSourceContext.class;
        }

        @Override
        public Class<? extends SCMSource> getSourceClass() {
            return GitHubSCMSource.class;
        }
    }

    private static final class CustomContextNotificationStrategy extends AbstractGitHubNotificationStrategy {

        private boolean shouldUnstableBeMarkedAsSuccess;

        CustomContextNotificationStrategy(boolean shouldUnstableBeMarkedAsSuccess) {
            this.shouldUnstableBeMarkedAsSuccess = shouldUnstableBeMarkedAsSuccess;
        }

        @Override
        public List<GitHubNotificationRequest> notifications(GitHubNotificationContext notificationContext, TaskListener listener) {
            GHCommitState commitState = notificationContext.getDefaultState(listener);
            Run<?, ?> build = notificationContext.getBuild();

            if (null != build) {
                Result result = build.getResult();
                if (null != result && result.equals(Result.UNSTABLE) && shouldUnstableBeMarkedAsSuccess) {
                    commitState = GHCommitState.SUCCESS;
                }
            }

            return Collections.singletonList(
                GitHubNotificationRequest.build(
                    notificationContext.getDefaultContext(listener),
                    notificationContext.getDefaultUrl(listener),
                    notificationContext.getDefaultMessage(listener),
                    commitState,
                    notificationContext.getDefaultIgnoreError(listener)));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CustomContextNotificationStrategy that = (CustomContextNotificationStrategy) o;
            return shouldUnstableBeMarkedAsSuccess == that.shouldUnstableBeMarkedAsSuccess;
        }

        @Override
        public int hashCode() {
            return Objects.hash(shouldUnstableBeMarkedAsSuccess);
        }
    }
}
