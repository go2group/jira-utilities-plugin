package com.go2group.jira.plugin.customfield;

import com.atlassian.jira.issue.changehistory.ChangeHistory;

import java.sql.Timestamp;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/13/14
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChangeHistoryComparator implements Comparator<ChangeHistory> {

        @Override
        public int compare(ChangeHistory x, ChangeHistory y) {
            // TODO: Handle null x or y values
            int startComparison = compare(x.getTimePerformed(), y.getTimePerformed());
            return startComparison != 0 ? startComparison
                    : compare(x.getTimePerformed(), y.getTimePerformed());
        }

        // I don't know why this isn't in Long...
        private static int compare(Timestamp a, Timestamp b) {
            //Order in order of first changed to last change
            return a.after(b) ? -1
                    : b.after(a) ? 1
                    : 0;
        }

}
