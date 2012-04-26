package net.wagstrom.research.github.v3;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.IssueEvent;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.client.IGitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IssueMinerV3 extends AbstractMiner {
    private static final String ISSUES_DISABLED_MESSAGE = "Issues are disabled for this repo";
    private IssueService service;

    private Logger log = LoggerFactory.getLogger(IssueMinerV3.class); // NOPMD

    private IssueMinerV3() {
    }

    public IssueMinerV3(IssueService service) {
        this();
        this.service = service;
    }

    public IssueMinerV3(IGitHubClient ghc) {
        this();
        service = new IssueService(ghc);
    }

    public Collection<Issue> getIssues(String username, String reponame, String state) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(IssueService.FILTER_STATE, state);
        try {
            return service.getIssues(username, reponame, params);
        } catch (RequestException r) {
            if (r.getError().getMessage().equals(ISSUES_DISABLED_MESSAGE)) {
                log.warn("Issues disabled for repository {}/{}", username, reponame);
                return null;
            }
            log.error("Request exception in getIssues {}/{}", new Object[]{username, reponame, r});
            log.warn("Message: {}", r.getError().getMessage());
            return null;
        } catch (IOException e) {
            log.error("IOException in getIssues {}/{}", new Object[]{username, reponame, e});
            return null;
        } catch (NullPointerException e) {
            log.error("NullPointerException in getIssues {}/{}", new Object[]{username, reponame, e});
            return null;
        }
    }

    public Collection<Issue> getOpenIssues(String username, String reponame) {
        return getIssues(username, reponame, IssueService.STATE_OPEN);
    }

    public Collection<Issue> getClosedIssues(String username, String reponame) {
        return getIssues(username, reponame, IssueService.STATE_CLOSED);
    }

    public Collection<Issue> getAllIssues(String username, String reponame) {
        Collection<Issue> openIssues = getOpenIssues(username, reponame);
        Collection<Issue> closedIssues = getClosedIssues(username, reponame);
        // simple hack to check if openIssues returned a null set
        if (openIssues != null) {
            openIssues.addAll(closedIssues);
            return openIssues;
        } else {
            return closedIssues;
        }
    }

    public Issue getIssue(String username, String reponame, int issueId) {
        try {
            return service.getIssue(username, reponame, issueId);
        } catch (IOException e) {
            log.error("IO Exception Fetching issue {}/{}:{}", new Object[]{username, reponame, issueId, e});
            return null;
        }
    }

    public List<Comment> getIssueComments(IRepositoryIdProvider repo, Issue issue) {
        return getComments(repo, issue.getNumber());
    }
    
    public List<Comment> getPullRequestComments(IRepositoryIdProvider repo, PullRequest pr) {
        return getComments(repo, pr.getNumber());
    }
    
    protected List<Comment> getComments(IRepositoryIdProvider repo, int id) {
        try {
            return service.getComments(repo, id);
        } catch (IOException e) {
            log.error("Exception fetching comments for issue/pullrequest {}:{}", new Object[]{repo.generateId(), id, e});
            return null;
        }
    }

    public Collection<IssueEvent> getIssueEvents(IRepositoryIdProvider repo, Issue issue) {
        try {
            return service.getIssueEvents(repo, issue);
        } catch (IOException e) {
            log.error("Exception fetching events for issue {}:{}", new Object[]{repo.generateId(), issue.getNumber(), e});
            return null;
        }
    }
}
