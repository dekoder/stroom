package stroom.gitrepo.client.presenter;

import stroom.document.client.event.DirtyUiHandlers;
import stroom.task.client.TaskMonitorFactory;

/**
 * Interface to handle the button pushes from the GitRepo Settings tab.
 */
public interface GitRepoSettingsUiHandlers extends DirtyUiHandlers {

    /**
     * 'Push' button event handler.
     */
    void onGitRepoPush(TaskMonitorFactory taskMonitorFactory);
}
