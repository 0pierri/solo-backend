package com.fdmgroup.backend.security;

import com.fdmgroup.backend.repository.TaskListRepository;
import com.fdmgroup.backend.repository.TaskRepository;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.Authentication;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    // Use ObjectFactories so they get loaded on-demand and don't result in a whole bunch of post-processing notices
    private final ObjectFactory<TaskListRepository> taskListRepoFactory;
    private final ObjectFactory<TaskRepository> taskRepoFactory;

    @Autowired
    public MethodSecurityConfiguration(ObjectFactory<TaskListRepository> taskListRepoFactory,
                                       ObjectFactory<TaskRepository> taskRepoFactory) {
        this.taskListRepoFactory = taskListRepoFactory;
        this.taskRepoFactory = taskRepoFactory;
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return methodSecurityExpressionHandler();
    }

    /**
     * Returns a SecurityExpressionHandler with Permission's package (com.fdmgroup.backend.security)
     * added to the SpEL search path so it doesn't have to be included in @PreAuthorize
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler() {
            @Override
            public StandardEvaluationContext createEvaluationContextInternal(final Authentication auth, final MethodInvocation mi) {
                StandardEvaluationContext evaluationContext = super.createEvaluationContextInternal(auth, mi);
                ((StandardTypeLocator) evaluationContext.getTypeLocator()).registerImport(Permission.class.getPackageName());
                return evaluationContext;
            }
        };

        // Also set the PermissionEvaluator here
        expressionHandler.setPermissionEvaluator(new CustomEntityPermissionEvaluator(taskListRepoFactory, taskRepoFactory));
        return expressionHandler;
    }
}
