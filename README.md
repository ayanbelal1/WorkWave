# Time Tracking Tool for Project Management

A comprehensive JavaFX application for tracking time spent on projects with role-based dashboards for Admin, Project Manager, and Team Member users.

## Features

### Admin Dashboard
- **User Access Control**: Manage user roles and permissions
- **Project Setup**: Create and manage projects with milestones
- **Time Tracking Integration**: Simulated integration with external tools
- **Project Reports**: Generate detailed time reports with charts

### Project Manager Dashboard
- **Task Assignment**: Assign tasks to team members with deadlines
- **Monitor Time Tracking**: View time distribution across projects and team members
- **Progress Review**: Track project progress with visual indicators

### Team Member Dashboard
- **Log Time**: Record time spent on tasks with descriptions
- **View Time Logs**: Review personal time entries with filtering
- **Update Task Status**: Change task status (To Do, In Progress, Completed, Blocked)

## Technology Stack

- **Java 17+**
- **JavaFX 21** for UI
- **Maven** for dependency management
- **H2 Database** (embedded) for data storage
- **FXML** for UI layouts
- **MVC Pattern** for clean architecture

## Project Structure

```
com.timetracker/
├── model/          # Data models (User, Project, Task, TimeLog)
├── controller/     # JavaFX controllers for each dashboard
├── service/        # Business logic services
├── dao/           # Data Access Objects for database operations
├── util/          # Utility classes (DatabaseManager, SessionManager)
└── view/          # FXML files for UI layouts
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Running the Application

1. **Clone and navigate to the project directory**

2. **Compile and run using Maven:**
   ```bash
   mvn clean javafx:run
   ```

3. **Alternative: Compile and run manually:**
   ```bash
   mvn clean compile
   mvn javafx:run
   ```

### Demo Accounts

The application comes with pre-configured demo accounts:

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Project Manager | pm1 | pm123 |
| Team Member | dev1 | dev123 |
| Team Member | dev2 | dev123 |

## Database

The application uses an embedded H2 database that automatically initializes with sample data:
- 4 demo users with different roles
- 3 sample projects
- 6 sample tasks
- Sample time log entries

Database file is created as `timetracker.mv.db` in the project root.

## Key Features Demonstrated

### Authentication & Authorization
- Role-based login system
- Session management
- Role-specific dashboard routing

### Data Management
- CRUD operations for all entities
- Relational data with foreign keys
- Transaction handling

### User Interface
- Responsive JavaFX layouts
- Charts and data visualization
- Form validation and error handling
- Table views with custom cell factories

### Business Logic
- Time tracking calculations
- Progress monitoring
- Task status management
- Report generation

## Architecture Highlights

### MVC Pattern
- **Models**: Plain Java objects representing data entities
- **Views**: FXML files defining UI layouts
- **Controllers**: Handle user interactions and coordinate between models and views

### DAO Pattern
- Separate data access layer for each entity
- Database connection management
- SQL query encapsulation

### Service Layer
- Business logic separation
- Authentication services
- Session management

## Extending the Application

The application is designed for easy extension:

1. **Add new user roles**: Extend `UserRole` enum and create corresponding dashboards
2. **Add new features**: Create new controllers and FXML files
3. **Integrate external APIs**: Extend service layer for real integrations
4. **Add reporting**: Extend chart components and data aggregation
5. **Add notifications**: Implement event-driven notifications

## Development Notes

- Uses JavaFX Properties for data binding
- Implements proper error handling and user feedback
- Follows JavaFX best practices for UI development
- Database schema supports future enhancements
- Modular design allows for easy testing and maintenance

## Future Enhancements

- Real-time notifications
- Export functionality (PDF, Excel)
- Advanced reporting and analytics
- Integration with external project management tools
- Mobile companion app
- Advanced user permissions and team management