var board = ChessBoard("board2", {
  draggable: true,
  dropOffBoard: "trash",
  sparePieces: true,
  onDrop: onDrop
});
const cols = "abcdefgh";
function ay(rep) {
  const po = {}; // position object
  const { squares } = rep;
  squares.forEach(row => {
    row.forEach(({ row, col, piece }) => {
      const key = "" + cols[col] + (row + 1);
      if (piece) {
        const c = piece.color === "BLACK" ? "b" : "w";
        const pts = piece.pt.pieceType;
        const ptc = pts.split(".")[2];
        let pt;
        if (ptc === "Knight") pt = "N";
        else pt = ptc[0];
        po[key] = c + pt;
      }
    });
  });
  console.log("apply position", po);
  board.position(po);
}
// const rep = {"squares":[[{"row":0,"col":0,"piece":{"pt":{"pieceType":"com.company.Rook"},"history":[],"color":"BLACK"}},{"row":0,"col":1,"piece":{"pt":{"pieceType":"com.company.Knight"},"history":[],"color":"BLACK"}},{"row":0,"col":2,"piece":{"pt":{"pieceType":"com.company.Bishop"},"history":[],"color":"BLACK"}},{"row":0,"col":3,"piece":{"pt":{"pieceType":"com.company.Queen"},"history":[],"color":"BLACK"}},{"row":0,"col":4,"piece":{"pt":{"pieceType":"com.company.King"},"history":[],"color":"BLACK"}},{"row":0,"col":5,"piece":{"pt":{"pieceType":"com.company.Bishop"},"history":[],"color":"BLACK"}},{"row":0,"col":6,"piece":{"pt":{"pieceType":"com.company.Knight"},"history":[],"color":"BLACK"}},{"row":0,"col":7,"piece":{"pt":{"pieceType":"com.company.Rook"},"history":[],"color":"BLACK"}}],[{"row":1,"col":0,"piece":{"pt":{"velocity":1,"color":"BLACK","pieceType":"com.company.Pawn"},"history":[],"color":"BLACK"}},{"row":1,"col":1,"piece":{"pt":{"velocity":1,"color":"BLACK","pieceType":"com.company.Pawn"},"history":[],"color":"BLACK"}},{"row":1,"col":2,"piece":{"pt":{"velocity":1,"color":"BLACK","pieceType":"com.company.Pawn"},"history":[],"color":"BLACK"}},{"row":1,"col":3,"piece":{"pt":{"velocity":1,"color":"BLACK","pieceType":"com.company.Pawn"},"history":[],"color":"BLACK"}},{"row":1,"col":4,"piece":{"pt":{"velocity":1,"color":"BLACK","pieceType":"com.company.Pawn"},"history":[],"color":"BLACK"}},{"row":1,"col":5,"piece":{"pt":{"velocity":1,"color":"BLACK","pieceType":"com.company.Pawn"},"history":[],"color":"BLACK"}},{"row":1,"col":6,"piece":{"pt":{"velocity":1,"color":"BLACK","pieceType":"com.company.Pawn"},"history":[],"color":"BLACK"}},{"row":1,"col":7,"piece":{"pt":{"velocity":1,"color":"BLACK","pieceType":"com.company.Pawn"},"history":[],"color":"BLACK"}}],[{"row":2,"col":0},{"row":2,"col":1},{"row":2,"col":2},{"row":2,"col":3},{"row":2,"col":4},{"row":2,"col":5},{"row":2,"col":6},{"row":2,"col":7}],[{"row":3,"col":0},{"row":3,"col":1},{"row":3,"col":2},{"row":3,"col":3},{"row":3,"col":4},{"row":3,"col":5},{"row":3,"col":6},{"row":3,"col":7}],[{"row":4,"col":0},{"row":4,"col":1},{"row":4,"col":2},{"row":4,"col":3},{"row":4,"col":4},{"row":4,"col":5},{"row":4,"col":6},{"row":4,"col":7}],[{"row":5,"col":0},{"row":5,"col":1},{"row":5,"col":2},{"row":5,"col":3},{"row":5,"col":4},{"row":5,"col":5},{"row":5,"col":6},{"row":5,"col":7}],[{"row":6,"col":0,"piece":{"pt":{"velocity":-1,"color":"WHITE","pieceType":"com.company.Pawn"},"history":[],"color":"WHITE"}},{"row":6,"col":1,"piece":{"pt":{"velocity":-1,"color":"WHITE","pieceType":"com.company.Pawn"},"history":[],"color":"WHITE"}},{"row":6,"col":2,"piece":{"pt":{"velocity":-1,"color":"WHITE","pieceType":"com.company.Pawn"},"history":[],"color":"WHITE"}},{"row":6,"col":3,"piece":{"pt":{"velocity":-1,"color":"WHITE","pieceType":"com.company.Pawn"},"history":[],"color":"WHITE"}},{"row":6,"col":4,"piece":{"pt":{"velocity":-1,"color":"WHITE","pieceType":"com.company.Pawn"},"history":[],"color":"WHITE"}},{"row":6,"col":5,"piece":{"pt":{"velocity":-1,"color":"WHITE","pieceType":"com.company.Pawn"},"history":[],"color":"WHITE"}},{"row":6,"col":6,"piece":{"pt":{"velocity":-1,"color":"WHITE","pieceType":"com.company.Pawn"},"history":[],"color":"WHITE"}},{"row":6,"col":7,"piece":{"pt":{"velocity":-1,"color":"WHITE","pieceType":"com.company.Pawn"},"history":[],"color":"WHITE"}}],[{"row":7,"col":0,"piece":{"pt":{"pieceType":"com.company.Rook"},"history":[],"color":"WHITE"}},{"row":7,"col":1,"piece":{"pt":{"pieceType":"com.company.Knight"},"history":[],"color":"WHITE"}},{"row":7,"col":2,"piece":{"pt":{"pieceType":"com.company.Bishop"},"history":[],"color":"WHITE"}},{"row":7,"col":3,"piece":{"pt":{"pieceType":"com.company.Queen"},"history":[],"color":"WHITE"}},{"row":7,"col":4,"piece":{"pt":{"pieceType":"com.company.King"},"history":[],"color":"WHITE"}},{"row":7,"col":5,"piece":{"pt":{"pieceType":"com.company.Bishop"},"history":[],"color":"WHITE"}},{"row":7,"col":6,"piece":{"pt":{"pieceType":"com.company.Knight"},"history":[],"color":"WHITE"}},{"row":7,"col":7,"piece":{"pt":{"pieceType":"com.company.Rook"},"history":[],"color":"WHITE"}}]]};
// ay(rep);

// Create WebSocket connection.
const socket = new WebSocket("ws://maxwells-macbook-pro.local:1099");

// Connection opened
socket.addEventListener("open", function(event) {
  console.log("open");
});

// Listen for errors
socket.addEventListener("error", function(event) {
  console.log("Error from server ", event);
});

let cancel = false;
// Listen for messages
socket.addEventListener("message", function(event) {
  console.log("Message from server ", event);
  const msgs = JSON.parse(event.data);
  msgs.forEach(({ type, body }) => {
    if (type === 2) {
      console.log(body);
      if (body === "check mate") alert("check mate");
      return;
    } else if (type === 1 || type === 3) {
      const boardRep = JSON.parse(body);
      console.log(boardRep);
      ay(boardRep);
    }
    if (type == 3) {
      cancel = false;
      function x() {
        if (cancel) return;
        // logKey();
        setTimeout(x, 1000);
      }
      setTimeout(x, 1000);
    }
  });
});

function sendMessage(kind, message) {
  socket.send(
    JSON.stringify({
      kind,
      message: JSON.stringify(message)
    })
  );
}

function onDrop(source, target, piece, newPos, oldPos, orientation) {
  const pl = JSON.stringify({
    source,
    target,
    piece
  });
  sendMessage(messageTypeMove, pl);
}
const input = document.querySelector("body");
input.addEventListener("keypress", sendAuto);

function sendAuto(e) {
  sendMessage(messageTypeAuto, {});
}
const messageTypeMove = 0;
const messageTypeAuto = 1;
const messageTypeReset = 2;
$("#autoBtn").on("click", () => {
  cancel = true;
  sendMessage(messageTypeReset, {});
});
