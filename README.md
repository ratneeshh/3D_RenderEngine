# 3D Graphics Engine in Java

A simple 3D graphics engine implemented in Java that renders basic 3D shapes using software rasterization.

## Features

- Software-based 3D rendering
- Z-buffer implementation for proper depth handling
- Mouse rotation controls
- Zoom functionality
- Supports various 3D shapes:
  - Pyramid
  - Cuboid
  - Diamond (Octahedron)

## Controls

- **Mouse Drag**: Rotate the shape
- **Mouse Wheel**: Zoom in/out

## Implementation Details

- Uses software rasterization without OpenGL
- Triangle-based rendering
- Implements basic 3D transformations (rotation, scaling)
- Color-per-face rendering

## Examples

The engine can render various 3D shapes including:
- Pyramids with square bases
- Rectangular prisms (cuboids)
- Diamond shapes (octahedrons)

## Requirements

- Java 8 or higher
- Swing for GUI components

## Usage

```java
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new Render());
        frame.setVisible(true);
    }
}
```
