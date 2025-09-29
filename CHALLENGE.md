# Mars Rover Challenge

A robotic rover is to be landed by NASA on a plateau on Mars. This plateau, which is curiously rectangular, must be navigated by the rovers so that their on-board cameras can get a complete view of the surrounding terrain to send back to Earth.

A rover's position and location is represented by a combination of **x** and **y** coordinates and a letter representing one of the four cardinal compass points. The plateau is divided up into a grid to simplify navigation.

An example position might be `0 0 N`, which means the rover is in the bottom left corner and facing North.

In order to control a rover, NASA sends a simple string of letters. The possible letters are:

- `L`: spin 90° left (without moving)
- `R`: spin 90° right (without moving)
- `M`: move forward one grid point, maintaining the same heading

If the rover tries to move and is heading to the limit of the plateau, it won’t move.

> Assume that the square directly North from `(x, y)` is `(x, y+1)`.

---

## Input

To ease the processing of the plateau creation and reading the rover’s movements, all data will be sent in **JSON format**:

- **topRightCorner**: indicates the upper-right coordinates of the plateau.
- **roverPosition**: indicates the rover’s initial position inside the plateau.
- **roverDirection**: indicates the rover’s initial heading.
- **movements**: instructions telling the rover how to explore the plateau.

---

## Output

The output for the rover should be its **final coordinates and heading**, separated by spaces.

---

## Example

**Input**:
```json
{
  "topRightCorner": { "x": 5, "y": 5 },
  "roverPosition": { "x": 1, "y": 2 },
  "roverDirection": "N",
  "movements": "LMLMLMLMM"
}
```

**Expected Output**: 1 3 N
