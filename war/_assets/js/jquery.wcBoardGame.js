/**
@file
	game.js
@brief
	Copyright 2011 Creative Crew. All rights reserved.
@author
	William Chang
	Email: william@creativecrew.org
	Website: http://www.williamchang.org
@version
	0.1
@date
	- Created: 2011-04-03
	- Modified: 2011-04-11
	.
@note
	Prerequisites:
	- jQuery http://www.jquery.com/
	- jQuery Plugin JSON http://code.google.com/p/jquery-json/
	- Google App Engine Channel API http://code.google.com/appengine/docs/java/channel/javascript.html
	.
	References:
	- http://diveintohtml5.org/canvas.html
	- http://diveintohtml5.org/examples/halma.js
	- http://www.quesucede.com/public/gameoflife/source.html
	- http://blog.robbychen.com/2010/03/14/learning-html-5-canvas/
	- http://jsperf.com/setinterval-vs-settimeout
	.
*/

// Widget: Board Game
(function($){
var memberPublic = {};
var _extensionName = 'wcBoardGame';
// Validate prerequisites.
if(typeof goog.appengine.Channel !== 'function') {throw('Dependency Error: Google App Engine Channel API is missing.');}
if(typeof goog.appengine.Socket !== 'function') {throw('Dependency Error: Google App Engine Channel API is missing.');}
if(typeof $.toJSON !== 'function') {throw('Dependency Error: jQuery plugin JSON is missing.');}
// Declare options and set default values.
var _opt = null, _optCustoms = null, _optDefaults = {
	strChannelOpenedUrl:'',
	strChannelCheckUrl:'',
	strChannelMoveUrl:'',
	strUserMeId:'',
	strGameKey:'',
	strChannelToken:'',
	strChannelMessageInitial:'',
	strStartButtonSelector:'.btn_start',
	strRestartButtonSelector:'.btn_start',
	strEndButtonSelector:'.btn_start',
	strConnectButtonSelector:'.btn_connect',
	strDisconnectButtonSelector:'.btn_disconnect',
	numPlayers:2,
	strTimerSelector:'#timer .value',
	numTimerSeconds:30,
	strScoresSelector:'#scores',
	strScoresHtml:'<div class=\"score\"><span class=\"name\"></span> : <span class=\"value\"></span> points</div>',
	strScoresNameSelector:'> .name',
	strScoresValueSelector:'> .value',
	numBoardTableRows:8,
	numBoardTableColumns:6,
	numBoardLineWidth:1
};

/* Private Fields
//-------------------------------------------------------------------*/

var _numBoardPixelWidth = 0;
var _numBoardPixelHeight = 0;
var _numBoardCellWidth = 50;
var _numBoardCellHeight = 50;
var _objBoardCells = []; // Two-dimension array.
var _objBoardPieces = []; // One-dimension array.

var _eleStartButton = null;
var _eleRestartButton = null;
var _eleEndButton = null;
var _eleConnectButton = null;
var _eleDisconnectButton = null;

var _eleScores = null;
var _eleTimer = null;
var _numTimerSeconds = 0;
var _eleCanvas = null;
var _ctxCanvas = null;

var _objPlayers = [];
var _objPlayerCurrent = null;

var _numGameState = 0;
var _enumGameState = {
	ENDED:0,
	NEW:1,
	RUNNING:2,
	WAITING:3
};

var _objChannelSocket = null;
var _objChannelData = {
	strPlayer1:'',
	numPlayer1Score:0,
	strPlayer2:'',
	numPlayer2Score:0,
	strPlayer3:'',
	numPlayer3Score:0,
	objBoard:null,
	strWinner:'',
	numState:3
};

/* Private Methods
//-------------------------------------------------------------------*/

/** On event, get cursor position. */
function _onGetCursorPosition(evt) {
	var numX, numY;
	if(evt.pageX || evt.pageY) {
		numX = evt.pageX;
		numY = evt.pageY;
	} else {
		numX = evt.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
		numY = evt.clientY + document.body.scrollTop + document.documentElement.scrollTop;
	}
	numX -= _eleCanvas.offsetLeft;
	numY -= _eleCanvas.offsetTop;
	numX = Math.min(numX, _opt.numBoardTableColumns * _numBoardCellWidth);
	numY = Math.min(numY, _opt.numBoardTableRows * _numBoardCellHeight);
	return new Cell(Math.floor(numY / _numBoardCellHeight), Math.floor(numX / _numBoardCellWidth), null);
}
/** On click event, canvas. */
function _onClickCanvas(evt) {
	// Validate state.
	if(_numGameState == _enumGameState.ENDED || _numGameState == _enumGameState.NEW || _numGameState == _enumGameState.WAITING) {return false;}
	// Declare and init prerequisites.
	var objCellSelected = _onGetCursorPosition(evt);
	var objCell = _objBoardCells[objCellSelected.numRow][objCellSelected.numColumn];
	// Validate piece.
	if(objCell.objPiece && objCell.objPiece.boolVisible && parseInt(objCell.objPiece.numPlayerId, 10) == _objPlayerCurrent.numId) {
		_removePiece(objCell);
		_setPlayerScore(_objPlayerCurrent, objCell.objPiece);
		// Update server-side via channel.
		_sendChannelMessage(_opt.strChannelMoveUrl, {
			userId:_objPlayerCurrent.strName,
			userScore:_objPlayerCurrent.numScore,
			board:$.toJSON(_objBoardPieces),
			timer:_numTimerSeconds,
			state:_numGameState
		});
	}
	// Prevent default action.
	return false;
}
/** On event, start game. */
function _onStartGame(evt) {
	// Validate state.
	if(_numGameState == _enumGameState.WAITING) {return false;}
	if($(this).attr('disabled') == 'disabled') {return false;}
	// Set button.
	$(this).attr('disabled', 'disabled').get(0);
	// Validate game state.
	if(_numGameState == _enumGameState.ENDED) {
		memberPublic.createGame();
	}
	// Run game.
	memberPublic.runGame();
	// Prevent default action.
	return false;
}
/** On event, disconnect game. */
function _onDisconnectGame(evt) {
	// Do something.
}
/** On event, channel messaged. */
function _onChannelMessaged(objMessage) {
	var objData = $.parseJSON(objMessage.data);
	// Set data.
	_objChannelData.strPlayer1 = objData.user1;
	_objChannelData.numPlayer1Score = objData.user1Score;
	_objChannelData.strPlayer2 = objData.user2;
	_objChannelData.numPlayer2Score = objData.user2Score;
	_objChannelData.strPlayer3 = objData.user3;
	_objChannelData.numPlayer3Score = objData.user3Score;
	_objChannelData.objBoard = _objBoardPieces = objData.board;
	_objChannelData.strWinner = objData.winner;
	_objChannelData.numState = _numGameState = objData.state;
	// Update game.
	_setTimer(objData.timer);
	if(objData.winner) {memberPublic.endGame();}
	_setPlayer(1, _objChannelData.strPlayer1, _objChannelData.numPlayer1Score);
	_setPlayer(2, _objChannelData.strPlayer2, _objChannelData.numPlayer2Score);
	_setPlayer(3, _objChannelData.strPlayer3, _objChannelData.numPlayer3Score);
	if(_objPlayers.length < _opt.numPlayers) {
		_numGameState = _enumGameState.WAITING;
	} else if(_numGameState == _enumGameState.RUNNING) {
		$(_eleStartButton).trigger('click');
		_drawPieces(_objBoardPieces);
	}
}
/** On event, channel opened. */
function _onChannelOpened() {
	_sendChannelMessage(_opt.strChannelOpenedUrl);
}
/** Send message through channel. */
function _sendChannelMessage(strUrl, objParameters) {
	// Validate channel.
	if(!_objChannelSocket) {return;}
	// Set AJAX parameters.
	var objAjaxParameters = null;
	if(objParameters) {objAjaxParameters = objParameters;}
	// Perform AJAX.
	$.post(strUrl + '?gameKey=' + _opt.strGameKey, objAjaxParameters, function(data, status) {
		if(status == 'success' && data == '0') {
			console.log(data);
		} else if(status == 'success') {
			// Do nothing.
		} else {throw(status);}
	});
}
/** Close (Comet) channel. */
function _closeChannel() {
	if(!_objChannelSocket) {return;}
	_objChannelSocket.close();
	_objChannelSocket = null;
}
/** Open (Comet) channel. */
function _openChannel() {
	var objChannel = new goog.appengine.Channel(_opt.strChannelToken);
	_objChannelSocket = objChannel.open({
		onopen:_onChannelOpened,
		onmessage:_onChannelMessaged,
		onerror:function() {},
		onclose:function() {}
	});
	_objChannelSocket.onopen = _onChannelOpened;
	_objChannelSocket.onmessage = _onChannelMessaged;
}
/** Create pieces (generate from client-side). */
function _createPieces() {
	var numIndex = 0, objCell = null, objPiece = null;
	_objBoardPieces = [];
	for(var numRow = 0;numRow < _opt.numBoardTableRows;numRow += 1) {
		_objBoardCells[numRow] = [];
		for(var numColumn = 0;numColumn <= _opt.numBoardTableColumns;numColumn += 1) {
			// Create object and add to collection.
			objPiece = new Piece(numIndex, Math.floor(Math.random() * _opt.numPlayers + 1), 1, true);
			_objBoardPieces[numIndex] = objPiece;
			// Draw piece.
			_drawPiece(numRow, numColumn, objPiece);
			// Create object and add to collections.
			_objBoardCells[numRow][numColumn] = new Cell(numRow, numColumn, objPiece);
			// Increment index.
			numIndex++;
		}
	}
}
/** Draw board. */
function _drawBoard() {
	// Clear canvas.
	_ctxCanvas.clearRect(0, 0, _numBoardPixelWidth, _numBoardPixelHeight);
	// Begin path.
	_ctxCanvas.beginPath();
	// Create vertical lines.
	for(var x = 0;x <= _numBoardPixelWidth;x += _numBoardCellWidth) {
		_ctxCanvas.moveTo(0.5 + x, 0);
		_ctxCanvas.lineTo(0.5 + x, _numBoardPixelHeight);
	}
	// Create horizontal lines.
	for(var y = 0;y <= _numBoardPixelHeight;y += _numBoardCellHeight) {
		_ctxCanvas.moveTo(0, 0.5 + y);
		_ctxCanvas.lineTo(_numBoardPixelWidth, 0.5 +  y);
	}
	// Draw lines.
	_ctxCanvas.lineWidth = _opt.numBoardLineWidth;
	_ctxCanvas.strokeStyle = '#ccc';
	_ctxCanvas.stroke();
}
/** Draw piece. */
function _drawPiece(numRow, numColumn, objPiece) {
	// Validate piece.
	if(objPiece && objPiece.boolVisible) {
		// Declare and init prerequisites.
		var numX = (numColumn * _numBoardCellWidth) + (_numBoardCellWidth / 2);
		var numY = (numRow * _numBoardCellHeight) + (_numBoardCellHeight / 2);
		var numRadius = (_numBoardCellWidth / 2) - (_numBoardCellWidth / 10);
		// Create structure.
		_ctxCanvas.beginPath();
		_ctxCanvas.arc(numX, numY, numRadius, 0, Math.PI * 2, false);
		_ctxCanvas.closePath();
		// Draw lines.
		_ctxCanvas.strokeStyle = '#000';
		_ctxCanvas.stroke();
		// Fill structure.
		_ctxCanvas.fillStyle = '#666';
		_ctxCanvas.fill();
		// Draw text.
		_ctxCanvas.textAlign = 'center';
		_ctxCanvas.textBaseline = 'middle';
		_ctxCanvas.font = 'bold 24px Verdana, Arial, Helvetica';
		_ctxCanvas.fillStyle = '#fff';
		_ctxCanvas.fillText(objPiece.numPlayerId, numX, numY);
	} else {
		// Declare and init prerequisites.
		var numX = numColumn * _numBoardCellWidth + _opt.numBoardLineWidth;
		var numY = numRow * _numBoardCellHeight + _opt.numBoardLineWidth;
		// Clear piece.
		_ctxCanvas.clearRect(numX, numY, _numBoardCellWidth - _opt.numBoardLineWidth, _numBoardCellHeight - _opt.numBoardLineWidth);
	}
}
/** Draw pieces from one-dimension array (retrieve from server-side). */
function _drawPieces(objBoardPieces) {
	var numRow = -1, numColumn = 0;
	for(var numIndex = 0;numIndex < objBoardPieces.length;numIndex += 1) {
		// Validate index.
		if((numIndex % _opt.numBoardTableColumns) == 0) {
			_objBoardCells[++numRow] = [];
			numColumn = 0;
		}
		// Draw piece.
		_drawPiece(numRow, numColumn,  objBoardPieces[numIndex]);
		// Creat object and add to collection.
		_objBoardCells[numRow][numColumn] = new Cell(numRow, numColumn, objBoardPieces[numIndex]);
		// Increment.
		numColumn++;
	}
}

/** Remove piece. */
function _removePiece(objCell) {
	var objPiece = objCell.objPiece;
	_objBoardPieces[objPiece.numIndex].boolVisible = objPiece.boolVisible = false;
	_drawPiece(objCell.numRow, objCell.numColumn, objPiece);
}
/** Run timer. */
function _runTimer() {
	if(_numGameState == _enumGameState.RUNNING && _numTimerSeconds > 0) {
		_setTimer(_numTimerSeconds - 1);
		setTimeout(_runTimer, 1000);
	} else {
		memberPublic.endGame();
	}
}
/** Set player. */
function _setPlayer(numId, strName, numScore) {
	if(!strName) {return null;}
	var objPlayer = null;
	// Check player exist.
	for(var numIndex = 0;numIndex < _objPlayers.length;numIndex += 1) {
		if(_objPlayers[numIndex].numId == numId) {
			objPlayer = _objPlayers[numIndex];
			break;
		}
	}
	if(objPlayer) {
		// Set player.
		objPlayer.numId = numId;
		objPlayer.strName = strName;
		objPlayer.numScore = numScore;
		// Set markup.
		$(objPlayer.eleScoreValue).text(numScore);
	} else {
		// Create markup.
		var eleScore = $(_opt.strScoresHtml).appendTo(_eleScores).get(0);
		var eleScoreValue = $(_opt.strScoresValueSelector, eleScore).text(numScore).get(0);
		$(_opt.strScoresNameSelector, eleScore).text(numId + '. '+ strName);
		// Create player.
		objPlayer = new Player(numId, strName, numScore, eleScore, eleScoreValue);
		_objPlayers.push(objPlayer);
	}
	// Validate current player.
	if(strName == _opt.strUserMeId) {
		_objPlayerCurrent = objPlayer;
	}
	return objPlayer;
}
/** Set player score. */
function _setPlayerScore(objPlayer, objPiece) {
	// Set score.
	objPlayer.numScore += objPiece.numValue;
	// Update markup.
	$(objPlayer.eleScoreValue).text(objPlayer.numScore);
}
/** Set timer. */
function _setTimer(numSeconds) {
	_numTimerSeconds = numSeconds;
	$(_eleTimer).text(numSeconds);
}

/* Public Methods
//-------------------------------------------------------------------*/

/** Extend core library. */
memberPublic = $[_extensionName] = function(eleCanvas, optCustoms) {
	// Merge two options, modifying the first.
	_opt = $.extend({}, _optDefaults, optCustoms);
	// Init.
	memberPublic.init(eleCanvas);
	// Return library's object.
	return this;
};
/** Init widget. */
memberPublic.init = function(eleCanvas) {
	// Init board.
	_numBoardPixelWidth = 1 + (_opt.numBoardTableColumns * _numBoardCellWidth);
	_numBoardPixelHeight = 1 + (_opt.numBoardTableRows * _numBoardCellHeight);
	_eleCanvas = eleCanvas;
	_eleCanvas.width = _numBoardPixelWidth;
	_eleCanvas.height = _numBoardPixelHeight;
	_ctxCanvas = eleCanvas.getContext('2d');
	_eleCanvas.addEventListener('click', _onClickCanvas, false);
	// Init status.
	_eleScores = $(_opt.strScoresSelector).get(0);
	_eleTimer = $(_opt.strTimerSelector).get(0);
	// Create game.
	memberPublic.createGame();
	// Update game from initial server-side page load.
	_onChannelMessaged({data:_opt.strChannelMessageInitial});
	// Bind events.
	_eleConnectButton = $(_opt.strConnectButtonSelector).bind('click', function(evt) {
		_openChannel();
		$(_eleConnectButton).hide();
		$(_eleDisconnectButton).show();
		// Prevent default action.
		return false;
	}).get(0);
	_eleDisconnectButton = $(_opt.strDisconnectButtonSelector).bind('click', function(evt) {
		_closeChannel();
		$(_eleDisconnectButton).hide();
		$(_eleConnectButton).show();
		// Prevent default action.
		return false;
	}).get(0);
	_eleStartButton = $(_opt.strStartButtonSelector).bind('click', _onStartGame).get(0);
};
/** Get options. */
memberPublic.getOptions = function() {
	return _opt;
};
/** Create (new) game. */
memberPublic.createGame = function() {
	// Draw board.
	_drawBoard();
	// Set timer.
	_setTimer(_opt.numTimerSeconds);
	// Set state.
	_numGameState = _enumGameState.NEW;
};
/** Run game. */
memberPublic.runGame = function() {
	_numGameState = _enumGameState.RUNNING;
	_runTimer();
	// Update server-side via channel.
	_sendChannelMessage(_opt.strChannelCheckUrl, {
		timer:_numTimerSeconds,
		state:_numGameState
	});
};
/** End game. */
memberPublic.endGame = function() {
	_numGameState = _enumGameState.ENDED;
	$(_eleStartButton).removeAttr('disabled');
	// Update server-side via channel.
	_sendChannelMessage(_opt.strChannelCheckUrl, {
		timer:_numTimerSeconds,
		state:_numGameState
	});
};

/* Custom Objects
//-------------------------------------------------------------------*/

function Player(numId, strName, numScore, eleScore, eleScoreValue) {
	this.numId = numId;
	this.strName = strName;
	this.numScore = numScore;
	this.eleScore = eleScore;
	this.eleScoreValue = eleScoreValue;
}
function Piece(numIndex, numPlayerId, numValue, boolVisible) {
	this.numIndex = numIndex;
	this.numPlayerId = numPlayerId;
	this.numValue = numValue;
	this.boolVisible = boolVisible;
}
function Cell(numRow, numColumn, objPiece) {
	this.numRow = numRow;
	this.numColumn = numColumn;
	this.objPiece = objPiece;
}

/* Chainability
//-------------------------------------------------------------------*/

/** Extend chain library. */
$.fn[_extensionName] = function(optCustoms) {
	// Merge two options, modifying the first.
	_opt = $.extend({}, _optDefaults, optCustoms);
	// Iterate and return each selected element back to library's chain.
	return this.each(function(_intIndex) {
		/** Init widget. */
		this.init = function() {
			memberPublic.init(_eleThis);
		};

		// Procedural.
		var _eleThis = this;
		_eleThis.init();
	});
};

})(jQuery);