package com.mycompany.myapp.util;

import io.micronaut.context.annotation.ConfigurationProperties;
import tech.jhipster.config.JHipsterDefaults;

@ConfigurationProperties("jhipster")
public class JHipsterProperties {

    private Cache cache;
    private Mail mail;
    private Logging logging;

    public JHipsterProperties(Cache cache, Mail mail, Logging logging) {
        this.cache = cache;
        this.mail = mail;
        this.logging = logging;
    }

    public Cache getCache() {
        return cache;
    }

    public Mail getMail() {
        return mail;
    }

    public Logging getLogging() {
        return logging;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public void setLogging(Logging logging) {
        this.logging = logging;
    }

    @ConfigurationProperties("logging")
    public static class Logging {

        private boolean useJsonFormat = false;
        private Logstash logstash = new Logstash();

        public boolean isUseJsonFormat() {
            return this.useJsonFormat;
        }

        public void setUseJsonFormat(boolean useJsonFormat) {
            this.useJsonFormat = useJsonFormat;
        }

        public Logstash getLogstash() {
            return this.logstash;
        }

        public void setLogstash(Logstash logstash) {
            this.logstash = logstash;
        }

        @ConfigurationProperties("logstash")
        public static class Logstash {

            private boolean enabled = false;
            private String host = "localhost";
            private int port = 5000;
            private int queueSize = 512;

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getHost() {
                return this.host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public int getPort() {
                return this.port;
            }

            public void setPort(int port) {
                this.port = port;
            }

            public int getQueueSize() {
                return this.queueSize;
            }

            public void setQueueSize(int queueSize) {
                this.queueSize = queueSize;
            }
        }
    }

    @ConfigurationProperties("mail")
    public static class Mail {

        private boolean enabled = false;
        private String from = "";
        private String baseUrl = "";

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getFrom() {
            return this.from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getBaseUrl() {
            return this.baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    @ConfigurationProperties("cache")
    public static class Cache {

        private Hazelcast hazelcast = new Hazelcast();
        private Caffeine caffeine = new Caffeine();
        private Ehcache ehcache = new Ehcache();
        private Infinispan infinispan = new Infinispan();
        private Memcached memcached = new Memcached();
        private Redis redis = new Redis();

        public Hazelcast getHazelcast() {
            return hazelcast;
        }

        public Caffeine getCaffeine() {
            return caffeine;
        }

        public Ehcache getEhcache() {
            return ehcache;
        }

        public Infinispan getInfinispan() {
            return infinispan;
        }

        public Memcached getMemcached() {
            return memcached;
        }

        public Redis getRedis() {
            return redis;
        }

        public void setHazelcast(Hazelcast hazelcast) {
            this.hazelcast = hazelcast;
        }

        public void setCaffeine(Caffeine caffeine) {
            this.caffeine = caffeine;
        }

        public void setEhcache(Ehcache ehcache) {
            this.ehcache = ehcache;
        }

        public void setInfinispan(Infinispan infinispan) {
            this.infinispan = infinispan;
        }

        public void setMemcached(Memcached memcached) {
            this.memcached = memcached;
        }

        public void setRedis(Redis redis) {
            this.redis = redis;
        }

        @ConfigurationProperties("memcached")
        public static class Memcached {

            private boolean enabled = false;
            private String servers = "localhost:11211";
            private int expiration = 300;
            private boolean useBinaryProtocol = true;

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getServers() {
                return this.servers;
            }

            public void setServers(String servers) {
                this.servers = servers;
            }

            public int getExpiration() {
                return this.expiration;
            }

            public void setExpiration(int expiration) {
                this.expiration = expiration;
            }

            public boolean isUseBinaryProtocol() {
                return this.useBinaryProtocol;
            }

            public void setUseBinaryProtocol(boolean useBinaryProtocol) {
                this.useBinaryProtocol = useBinaryProtocol;
            }
        }

        @ConfigurationProperties("infinispan")
        public static class Infinispan {

            private String configFile = "default-configs/default-jgroups-tcp.xml";
            private boolean statsEnabled = false;
            private final Local local = new Local();
            private final Distributed distributed = new Distributed();
            private final Replicated replicated = new Replicated();

            public String getConfigFile() {
                return this.configFile;
            }

            public void setConfigFile(String configFile) {
                this.configFile = configFile;
            }

            public boolean isStatsEnabled() {
                return this.statsEnabled;
            }

            public void setStatsEnabled(boolean statsEnabled) {
                this.statsEnabled = statsEnabled;
            }

            public Local getLocal() {
                return this.local;
            }

            public Distributed getDistributed() {
                return this.distributed;
            }

            public Replicated getReplicated() {
                return this.replicated;
            }

            @ConfigurationProperties("replicated")
            public static class Replicated {

                private long timeToLiveSeconds = 60L;
                private long maxEntries = 100L;

                public long getTimeToLiveSeconds() {
                    return this.timeToLiveSeconds;
                }

                public void setTimeToLiveSeconds(long timeToLiveSeconds) {
                    this.timeToLiveSeconds = timeToLiveSeconds;
                }

                public long getMaxEntries() {
                    return this.maxEntries;
                }

                public void setMaxEntries(long maxEntries) {
                    this.maxEntries = maxEntries;
                }
            }

            @ConfigurationProperties("distributed")
            public static class Distributed {

                private long timeToLiveSeconds = 60L;
                private long maxEntries = 100L;
                private int instanceCount = 1;

                public long getTimeToLiveSeconds() {
                    return this.timeToLiveSeconds;
                }

                public void setTimeToLiveSeconds(long timeToLiveSeconds) {
                    this.timeToLiveSeconds = timeToLiveSeconds;
                }

                public long getMaxEntries() {
                    return this.maxEntries;
                }

                public void setMaxEntries(long maxEntries) {
                    this.maxEntries = maxEntries;
                }

                public int getInstanceCount() {
                    return this.instanceCount;
                }

                public void setInstanceCount(int instanceCount) {
                    this.instanceCount = instanceCount;
                }
            }

            @ConfigurationProperties("local")
            public static class Local {

                private long timeToLiveSeconds = 60L;
                private long maxEntries = 100L;

                public long getTimeToLiveSeconds() {
                    return this.timeToLiveSeconds;
                }

                public void setTimeToLiveSeconds(long timeToLiveSeconds) {
                    this.timeToLiveSeconds = timeToLiveSeconds;
                }

                public long getMaxEntries() {
                    return this.maxEntries;
                }

                public void setMaxEntries(long maxEntries) {
                    this.maxEntries = maxEntries;
                }
            }
        }

        @ConfigurationProperties("caffeine")
        public static class Caffeine {

            private int timeToLiveSeconds = 3600;
            private long maxEntries = 100;

            public int getTimeToLiveSeconds() {
                return timeToLiveSeconds;
            }

            public void setTimeToLiveSeconds(int timeToLiveSeconds) {
                this.timeToLiveSeconds = timeToLiveSeconds;
            }

            public long getMaxEntries() {
                return maxEntries;
            }

            public void setMaxEntries(long maxEntries) {
                this.maxEntries = maxEntries;
            }
        }

        @ConfigurationProperties("ehcache")
        public static class Ehcache {

            private int timeToLiveSeconds = 3600;
            private long maxEntries = 100L;

            public int getTimeToLiveSeconds() {
                return this.timeToLiveSeconds;
            }

            public void setTimeToLiveSeconds(int timeToLiveSeconds) {
                this.timeToLiveSeconds = timeToLiveSeconds;
            }

            public long getMaxEntries() {
                return this.maxEntries;
            }

            public void setMaxEntries(long maxEntries) {
                this.maxEntries = maxEntries;
            }
        }

        @ConfigurationProperties("hazelcast")
        public static class Hazelcast {

            private int timeToLiveSeconds = 3600;
            private int backupCount = 1;
            private final ManagementCenter managementCenter = new ManagementCenter();

            public ManagementCenter getManagementCenter() {
                return this.managementCenter;
            }

            public int getTimeToLiveSeconds() {
                return this.timeToLiveSeconds;
            }

            public void setTimeToLiveSeconds(int timeToLiveSeconds) {
                this.timeToLiveSeconds = timeToLiveSeconds;
            }

            public int getBackupCount() {
                return this.backupCount;
            }

            public void setBackupCount(int backupCount) {
                this.backupCount = backupCount;
            }

            @ConfigurationProperties("management-center")
            public static class ManagementCenter {

                private boolean enabled = false;
                private int updateInterval = 3;
                private String url = "";

                public boolean isEnabled() {
                    return this.enabled;
                }

                public void setEnabled(boolean enabled) {
                    this.enabled = enabled;
                }

                public int getUpdateInterval() {
                    return this.updateInterval;
                }

                public void setUpdateInterval(int updateInterval) {
                    this.updateInterval = updateInterval;
                }

                public String getUrl() {
                    return this.url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }
        }

        @ConfigurationProperties("redis")
        public static class Redis {

            private String[] server;
            private int expiration;
            private boolean cluster;
            private int connectionPoolSize;
            private int connectionMinimumIdleSize;
            private int subscriptionConnectionPoolSize;
            private int subscriptionConnectionMinimumIdleSize;

            public Redis() {
                this.server = JHipsterDefaults.Cache.Redis.server;
                this.expiration = 300;
                this.cluster = false;
                this.connectionPoolSize = 64;
                this.connectionMinimumIdleSize = 24;
                this.subscriptionConnectionPoolSize = 50;
                this.subscriptionConnectionMinimumIdleSize = 1;
            }

            public String[] getServer() {
                return server;
            }

            public void setServer(String[] server) {
                this.server = server;
            }

            public int getExpiration() {
                return expiration;
            }

            public void setExpiration(int expiration) {
                this.expiration = expiration;
            }

            public boolean isCluster() {
                return cluster;
            }

            public void setCluster(boolean cluster) {
                this.cluster = cluster;
            }

            public int getConnectionPoolSize() {
                return connectionPoolSize;
            }

            public void setConnectionPoolSize(int connectionPoolSize) {
                this.connectionPoolSize = connectionPoolSize;
            }

            public int getConnectionMinimumIdleSize() {
                return connectionMinimumIdleSize;
            }

            public void setConnectionMinimumIdleSize(int connectionMinimumIdleSize) {
                this.connectionMinimumIdleSize = connectionMinimumIdleSize;
            }

            public int getSubscriptionConnectionPoolSize() {
                return subscriptionConnectionPoolSize;
            }

            public void setSubscriptionConnectionPoolSize(int subscriptionConnectionPoolSize) {
                this.subscriptionConnectionPoolSize = subscriptionConnectionPoolSize;
            }

            public int getSubscriptionConnectionMinimumIdleSize() {
                return subscriptionConnectionMinimumIdleSize;
            }

            public void setSubscriptionConnectionMinimumIdleSize(int subscriptionConnectionMinimumIdleSize) {
                this.subscriptionConnectionMinimumIdleSize = subscriptionConnectionMinimumIdleSize;
            }
        }
    }
}
