/**
 *  댓글 기능
 */
 
$(document).ready(function(){
	getAllReply(); // 함수 호출	
	
	// 댓글 작성 기능
	$('#btnAdd').click(function(){
		var boardId = $('#boardId').val(); // 게시판 번호 데이터
		// var memberId = $('#memberId').val(); // 작성자 데이터
		var replyContent = $('#replyContent').val(); // 댓글 내용
		
		if(replyContent.trim() === '') {
			alert('내용을 입력해 주세요.');
			return;
		}
		
		// javascript 객체 생성
		var obj = {
				'boardId' : boardId,
				'replyContent' : replyContent
		}
		console.log(obj);
		
		// $.ajax로 송수신
		$.ajax({
			type : 'POST', // 메서드 타입
			url : '../reply', // url
			headers : { // 헤더 정보
				'Content-Type' : 'application/json' // json content-type 설정
			}, 
			data : JSON.stringify(obj), // JSON으로 변환
			success : function(result) { // 전송 성공 시 서버에서 result 값 전송
				console.log(result);
				if(result == 1) {
					alert('댓글 입력 성공');
					getAllReply();
				}
			}
		});
	}); // end btnAdd.click()
	
	// 게시판 댓글 전체 가져오기
	function getAllReply() {
		let boardId = $('#boardId').val();
		console.log(boardId);
		let url = '../reply/all/' + boardId;
		$.getJSON(
			url, 		
			function(data) {
				// data : 서버에서 전송받은 list 데이터가 저장되어 있음.
				// getJSON()에서 json 데이터는 
				// javascript object로 자동 parsing됨.
				console.log(data);
				
				var list = ''; // 댓글 데이터를 HTML에 표현할 문자열 변수
				var memberId = $('#memberId').val(); // 로그인한 사용자 아이디
				
				console.log('로그인 아이디 : ' + memberId);
				// $(컬렉션).each() : 컬렉션 데이터를 반복문으로 꺼내는 함수
				$(data).each(function(){
					// this : 컬렉션의 각 인덱스 데이터를 의미
					console.log(this);
				  
					// 전송된 replyDateCreated는 문자열 형태이므로 날짜 형태로 변환이 필요
					var replyDateCreated = new Date(this.replyDateCreated);
					
					var hidden = '';
					var readonly = '';
					
					if(memberId != this.memberId){
							hidden = 'hidden';
							readonly = 'readonly';
					}

					list += '<div class="reply_item">'
						+ '<pre>'
						+ '<input type="hidden" id="replyId" value="'+ this.replyId +'">'
						+ '<input type="hidden" id="memberId" value="'+ this.memberId +'">'
						+ this.memberId
						+ '&nbsp;&nbsp;' // 공백
						+ '<input type="text" '+ readonly +' id="replyContent" value="'+ this.replyContent +'">'
						+ '&nbsp;&nbsp;'
						+ replyDateCreated
						+ '&nbsp;&nbsp;'
						+ '<button class="btn_update" '+ hidden +' >수정</button>'
						+ '<button class="btn_delete" '+ hidden +' >삭제</button>'
						+ '</pre>'
						+ '</div>';
				}); // end each()
					
				$('#replies').html(list); // 저장된 데이터를 replies div 표현
			} // end function()
		); // end getJSON()
	} // end getAllReplies()
	
	// 수정 버튼을 클릭하면 선택된 댓글 수정
	$('#replies').on('click', '.reply_item .btn_update', function(){
		console.log(this);
		
		// 선택된 댓글의 replyId, replyContent 값을 저장
		// prevAll() : 선택된 노드 이전에 있는 모든 형제 노드를 접근
		var replyId = $(this).prevAll('#replyId').val();
		var replyContent = $(this).prevAll('#replyContent').val();
		// var memberId = $(this).prevAll('#memberId').val(); // 댓글 작성자	
		console.log("선택된 댓글 번호 : " + replyId + ", 댓글 내용 : " + replyContent);
		
		if(replyContent.trim() === '') {
			alert('내용을 입력해 주세요.');
			return;
		}
		
		var obj = {
				'memberId' : memberId,
				'replyContent' : replyContent
		}
		
		// ajax 요청
		$.ajax({
			type : 'PUT', 
			url : '../reply/' + replyId,
			headers : {
				'Content-Type' : 'application/json'
			},
			data : JSON.stringify(obj), // JSON으로 변환
			success : function(result) {
				console.log(result);
				if(result == 1) {
					alert('댓글 수정 성공!');
					getAllReply();
				}
			}
		});
		
	}); // end replies.on()
	
	// 삭제 버튼을 클릭하면 선택된 댓글 삭제
	$('#replies').on('click', '.reply_item .btn_delete', function(){
		console.log(this);
		var boardId = $('#boardId').val(); // 게시판 번호 데이터
		var replyId = $(this).prevAll('#replyId').val();
		
		// ajax 요청
		$.ajax({
			type : 'DELETE', 
			url : '../reply/' + replyId + '/' + boardId, 
			headers : {
				'Content-Type' : 'application/json'
			},
			success : function(result) {
				console.log(result);
				if(result == 1) {
					alert('댓글 삭제 성공!');
					getAllReply();
				}
			}
		});
	}); // end replies.on()		

}); // end document()