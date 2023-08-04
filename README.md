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
- [For Windows](https://phoenixnap.com/kb/install-maven-windows)
- [For MacOS](https://www.digitalocean.com/community/tutorials/install-maven-mac-os)
- [For Ubuntu Linux (Should come pre-installed)](https://www.digitalocean.com/community/tutorials/install-maven-linux-ubuntu)

I've only tried this on Windows and Linux.

### Run the project:
Navigate to the root folder of the project in a command line and run
<br/>```java -jar target\SFEngine.jar``` (Windows) or
<br/>```java -jar target/SFEngine.jar``` (MacOS & Linux)

Ignore the warning shown on the first two lines when the project lauches.

