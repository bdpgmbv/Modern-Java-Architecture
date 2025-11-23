1. CORE SPRING BOOT & DEPENDENCY INJECTION
Modern Techniques:

Constructor Injection (modern) vs Field Injection (old @Autowired)
Lombok integration (@RequiredArgsConstructor)
Java Records for DTOs (Java 14+)
@Configuration and @Bean programmatic configuration
Conditional Beans (@ConditionalOnProperty, @ConditionalOnBean)
Application Events (Spring Events vs old Observer pattern)

Practice Project 1: E-Commerce Inventory System
Features to implement:
- Use constructor injection everywhere (no field injection)
- Create Product, Category, Supplier entities with Records for DTOs
- Implement custom conditional beans based on profiles
- Use Spring Events for inventory updates (publish-subscribe pattern)
- Practice @Primary, @Qualifier for multiple implementations
- Implement custom auto-configuration with @ConfigurationProperties
Old vs New:

❌ Old: @Autowired field injection
✅ New: Constructor injection with Lombok or Records
