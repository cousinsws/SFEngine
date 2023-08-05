# SFEngine
A graphing editor and analysis tool by Samuel Cousins.

Uses JavaFX to create visual diagrams of graphs backed by adjacency matrix, and uses JAMA Linear Algebra library to analyze various aspects of graph, including:
- Component # (with coloring)
- Eulerian quality
- Toughness
- And more...

## Compilation & Usage
The project is compiled with Maven, a command-line tool.
### Download Maven:
First, make sure [Java is up to date](https://www.oracle.com/java/technologies/downloads/).
- [For Windows](https://phoenixnap.com/kb/install-maven-windows)
- [For MacOS](https://www.digitalocean.com/community/tutorials/install-maven-mac-os)
- [For Ubuntu Linux (Should come pre-installed)](https://www.digitalocean.com/community/tutorials/install-maven-linux-ubuntu)

I've only tried this on Windows and Linux.

### Run the project:
Navigate to the root folder of the project in a command line and run

```mvn clean install```

once the process completes, run
<br/>```java -jar target\SFEngine.jar``` (Windows) or
<br/>```java -jar target/SFEngine.jar``` (MacOS & Linux)

(or navigate to the `target` directory and manually run the `jar` file)

Ignore the warning shown on the first two lines when the project launches.

# Contact
This project is a work-in-progress and user experience is not a priority.

Reach out to me at `sam.cousins3@gmail.com` for questions or inquiry.
