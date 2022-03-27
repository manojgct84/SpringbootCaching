# SpringbootCaching
Default Spring boot caching

# Simple Spring Boot Cache

     @Cacheable(value=CACHE_NAME, key="#id")
     @CachePut(value=CACHE_NAME, key="#city.getId()")
     @CacheEvict(value=CACHE_NAME, key="#id")

# How to use multiple keys in @Cacheable
     @Cacheable(cacheNames = CACHE_NAME, key = "#cityId +'-'+ #sortBy")