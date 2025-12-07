/**
 * 첨부 파일 업로드 기능 jquery 코드
 */
 
$(document).ready(function(){
	// 파일 객체를 배열로 전달받아 검증하는 함수
	function validateAttachs(files){
		// 차단할 확장자 정규식 
		var blockedExtensions = /\.(exe|sh|php|jsp|aspx|zip|alz|jpg|jpeg|png|gif)$/i; 
		var maxSize = 10 * 1024 * 1024; // 10 MB 
		
		if(files.length > 3) { // 파일 개수 제한
			alert("파일은 최대 3개만 가능합니다.");
			return false;
		}
		
		for(var i = 0; i < files.length; i++) {
			console.log(files[i]);
			var fileName = files[i].name; // 파일 이름
			var fileSize = files[i].size; // 파일 크기
			
			if (blockedExtensions.test(fileName)) { // 차단된 확장자인 경우
				alert("이 확장자의 파일은 첨부할 수 없습니다.");
				return false;
			}
			
			if (fileSize > maxSize) {
				alert("파일 크기가 너무 큽니다. 최대 크기는 10MB입니다.");
				return false;
			}
		}
		
		return true;
	} // end validateAttachs()
				
	// 첨부 파일을 선택하면 이벤트 발생
	// 선택된 첨부 파일을 첨부 파일 목록에 출력하고, 서버로 전송
	$('#attachInput').on('change', function(event){
		// event.target.files : input 태그에 선택된 파일 객체를 불러오는 코드
		var files = event.target.files;
	    var attachList = $('.attach-list'); // attach-list 요소 참조

	    if(files.length == 0) {
	    	return;
	    }
	    
	    if(!validateAttachs(files)) { // 파일 검증
	    	// 선택된 파일 정보 삭제
	    	$('#attachInput').val('');
	    	return;
	    }
	    
	    attachList.empty(); // 자식 요소 삭제
	    
	    $('.attachDTOFile-list').empty(); // 기존 선택 목록 초기화
	    


		/* 첨부 파일 비동기로 전송하는 코드 */ 
	    var formData = new FormData();
	    
      	for(var i = 0; i < files.length; i++){
        	formData.append("files", files[i]); 
     	}
      	
		$.ajax({
			type : 'post', 
			url : '../attach', 
			data : formData,
			processData : false,
			contentType : false,
			success : function(data) {
				console.log(data);
				var list = '';
				$(data).each(function(){
					// this : 컬렉션의 각 인덱스 데이터를 의미
					console.log(this);
				  	var attachDTO = this; // attachDTO 저장
				  	// encodeURIComponent() : 문자열에 포함된 특수 기호를 UTF-8로 
				  	// 인코딩하여 이스케이프시퀀스로 변경하는 함수 
					var attachPath = encodeURIComponent(this.attachPath);

					// input 태그 생성 
					// - type = hidden
					// - name = attachDTO
					// - data-chgName = attachtDTO.attachChgName
					var input = $('<input>').attr('type', 'hidden')
						.attr('name', 'attachDTO')
						.attr('data-chgName', attachDTO.attachChgName);
					
					// attachDTO를 JSON 데이터로 변경
					// - object 형태로 데이터 인식 불가능
					input.val(JSON.stringify(attachDTO));
					
       				// div에 input 태그 추가
        			$('.attachDTOFile-list').append(input);
				  	
				    // 첨부 파일 목록 출력
				    list += '<div class="attach_item" data-chgName="'+ this.attachChgName +'">'
				    	+ '<pre>'
				    	+ '<input type="hidden" id="attachPath" value="'+ this.attachPath +'">'
				    	+ '<input type="hidden" id="attachChgName" value="'+ attachDTO.attachChgName +'">'
				        + '<div>'
				        + attachDTO.attachRealName 
				        + "." + attachDTO.attachExtension
				        + '</div>'
				        + '<button class="attach_delete" >x</button>'
				        + '</pre>'
				        + '</div>';
				}); // end each()
				
				console.log(list);
				// list 문자열 attach-list div 태그에 적용
				$('.attach-list').html(list);
			} // end success
		
		}); // end $.ajax()

	}); // end attachInput.on()
	
	// 선택된 첨부 파일 목록 삭제
	// 선택된 파일 정보 전송를 서버로 전송
	$('.attach-list').on('click', '.attach_item .attach_delete', function(){
		console.log(this);
		if(!confirm('삭제하시겠습니까?')) {
			return;
		}
		var attachPath = $(this).prevAll('#attachPath').val();
		var attachChgName = $(this).prevAll('#attachChgName').val();

		// ajax 요청
		$.ajax({
			type : 'POST', 
			url : '../attach/delete', 
			data : {
				attachPath : attachPath, 
				attachChgName : attachChgName,					
			}, 
			success : function(result) {
				console.log(result);
				if(result == 1) {
					$('.attach-list').find('div')
				    .filter(function() {
				    	// data-chgName이 선택된 파일 이름과 같은 경우
				        return $(this).attr('data-chgName') === attachChgName;
				    })
				    .remove();
				    
				    $('.attachDTOFile-list').find('input')
				    .filter(function() {
				    	// data-chgName이 삭제 선택된 파일 이름과 같은 경우
				        return $(this).attr('data-chgName') === attachChgName;
				    })
				    .remove();

				}

			}
		}); // end ajax()
		
	}); // end attach-list.on()
	
}); // end document.ready()