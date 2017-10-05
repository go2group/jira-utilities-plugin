package com.go2group.jira.plugin.jql;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.JiraDataType;
import com.atlassian.jira.JiraDataTypes;
import com.atlassian.jira.jql.operand.QueryLiteral;
import com.atlassian.jira.jql.query.QueryCreationContext;
import com.atlassian.jira.plugin.jql.function.AbstractJqlFunction;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.query.clause.TerminalClause;
import com.atlassian.query.operand.FunctionOperand;

public class GroupsOfCurrentUserFunction extends AbstractJqlFunction{

    private final GroupManager groupManager;
    private static final Logger log = Logger.getLogger(GroupsOfCurrentUserFunction.class);

    public GroupsOfCurrentUserFunction(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    @Override
    public JiraDataType getDataType() {
        return JiraDataTypes.GROUP;
    }

    @Override
    public int getMinimumNumberOfExpectedArguments() {
        return 0;
    }

    @Override
    public List<QueryLiteral> getValues(QueryCreationContext context, FunctionOperand operand, TerminalClause clause) {

        final List<QueryLiteral> literals = new LinkedList<QueryLiteral>();

    	ApplicationUser currentUser = context.getApplicationUser();

        Collection<Group> groups = groupManager.getGroupsForUser(currentUser);

        if (groups != null){
            for (Group g : groups){
                if (g != null){
                    literals.add(new QueryLiteral(operand,g.getName()));
                }
            }
        }

        return literals;
    }

    @Override
    public MessageSet validate(ApplicationUser user, FunctionOperand operand, TerminalClause clause) {
        return validateNumberOfArgs(operand, 0);
    }

}
