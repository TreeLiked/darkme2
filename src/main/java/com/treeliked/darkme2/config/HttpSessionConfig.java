package com.treeliked.darkme2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * session config
 *
 * @author lqs2
 * @date 2018-12-18, Tue
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 180)
public class HttpSessionConfig {
}
