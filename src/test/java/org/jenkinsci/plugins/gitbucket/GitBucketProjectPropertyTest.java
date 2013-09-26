package org.jenkinsci.plugins.gitbucket;

import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixConfiguration;
import hudson.matrix.MatrixProject;
import hudson.matrix.MatrixRun;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import java.util.Collection;
import java.util.Iterator;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link GitBucketProjectProperty} class.
 *
 * @author sogabe
 */
public class GitBucketProjectPropertyTest {

    private static final String GITBUCKET_URL = "http://localhost/gitbucket/sogabe/gitbucket-plugin/";
    
    private GitBucketProjectProperty target;

    @Test
    public void testNormalizeUrl_NotEndWithSlash() {
        // not end with slash
        String url = "http://localhost/gitbucket/sogabe/gitbucket-plugin";
        target = new GitBucketProjectProperty(url, true);

        String actual = target.getUrl();

        assertThat(actual, is(url + '/'));
    }

    @Test
    public void testNormalizeUrl_EndWithSpace() {
        // end with " "
        String url = "http://localhost/gitbucket/sogabe/gitbucket-plugin  ";
        target = new GitBucketProjectProperty(url, true);

        String actual = target.getUrl();

        assertThat(actual, is(url.trim() + '/'));
    }

    @Test
    public void testNormalizeUrl_Empty() {
        // empty url
        String url = "  ";
        target = new GitBucketProjectProperty(url, true);

        String actual = target.getUrl();

        assertThat(actual, nullValue());
    }

    @Test
    public void testNormalizeUrl_Null() {
        // null
        String url = null;
        target = new GitBucketProjectProperty(url, true);

        String actual = target.getUrl();

        assertThat(actual, nullValue());
    }

    @Test
    public void testGetJobActions_UrlNotSet() {
        AbstractProject<?, ?> job = mock(AbstractProject.class);
        String url = null;
        target = new GitBucketProjectProperty(url, true);

        Collection<? extends Action> actual = target.getJobActions(job);

        assertThat(actual, notNullValue());
        assertThat(actual.isEmpty(), is(true));
    }

    @Test
    public void testGetJobActions_UrlSet() {
        AbstractProject<?, ?> job = mock(AbstractProject.class);
        target = new GitBucketProjectProperty(GITBUCKET_URL, true);

        Collection<? extends Action> actual = target.getJobActions(job);

        assertThat(actual, notNullValue());
        assertThat(actual.size(), is(1));

        Iterator<? extends Action> it = actual.iterator();
        Action action = it.next();
        assertThat(action, instanceOf(GitBucketLinkAction.class));

        GitBucketLinkAction linkAction = (GitBucketLinkAction) action;
        assertThat(linkAction.getUrlName(), is(GITBUCKET_URL));
    }

    @Test
    public void testGet_BuildNull() {
        AbstractBuild<?, ?> build = null;

        GitBucketProjectProperty actual = GitBucketProjectProperty.get(build);

        assertThat(actual, nullValue());
    }

    @Test
    public void testGet_FreeStyleProject() {
        AbstractProject job = mock(FreeStyleProject.class);
        AbstractBuild build = mock(FreeStyleBuild.class);
        GitBucketProjectProperty gpp = new GitBucketProjectProperty(GITBUCKET_URL, true);

        when(build.getProject()).thenReturn(job);
        when(job.getProperty(GitBucketProjectProperty.class)).thenReturn(gpp);

        GitBucketProjectProperty actual = GitBucketProjectProperty.get(build);

        assertThat(actual, notNullValue());
        assertThat(actual, sameInstance(gpp));
    }

    @Test
    public void testGet_MatrixBuild() {
        AbstractProject job = mock(MatrixProject.class);
        AbstractBuild build = mock(MatrixBuild.class);
        GitBucketProjectProperty gpp = new GitBucketProjectProperty(GITBUCKET_URL, true);

        when(build.getProject()).thenReturn(job);
        when(job.getProperty(GitBucketProjectProperty.class)).thenReturn(gpp);

        GitBucketProjectProperty actual = GitBucketProjectProperty.get(build);

        assertThat(actual, notNullValue());
        assertThat(actual, sameInstance(gpp));
    }

    @Test
    public void testGet_MatrixRun() {
        MatrixProject job = mock(MatrixProject.class);
        MatrixConfiguration mc = mock(MatrixConfiguration.class);
        AbstractBuild build = mock(MatrixRun.class);
        GitBucketProjectProperty gpp = new GitBucketProjectProperty(GITBUCKET_URL, true);

        when(build.getProject()).thenReturn(mc);
        when(mc.getParent()).thenReturn(job);
        when(job.getProperty(GitBucketProjectProperty.class)).thenReturn(gpp);

        GitBucketProjectProperty actual = GitBucketProjectProperty.get(build);

        assertThat(actual, notNullValue());
        assertThat(actual, sameInstance(gpp));
    }
    
}