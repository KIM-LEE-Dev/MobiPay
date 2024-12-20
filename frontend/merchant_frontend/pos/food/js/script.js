let lpnumber = null;
const url = "https://merchant.mobipay.kr/api/v1";
const MERCHANT_TYPE = 'FOOD';
const MERCHANT_TYPE_URL = 'food';
const MER_API_KEY = 'Da19J03F6g7H8iB2c54e';

async function loadDatabase() {
  const db = await idb.openDB("mobi_merchant_fnb", 1, {
    upgrade(db, oldVersion, newVersion, transaction) {
      db.createObjectStore("products", {
        keyPath: "id",
        autoIncrement: true,
      });
      db.createObjectStore("sales", {
        keyPath: "id",
        autoIncrement: true,
      });
    },
  });

  return {
    db,
    getProducts: async () => await db.getAll("products"),
    addProduct: async (product) => await db.add("products", product),
    editProduct: async (product) => await db.put("products", product),
    deleteProduct: async (product) => await db.delete("products", product.id),
    clearProducts: async () => {
      const tx = db.transaction("products", "readwrite");
      await tx.objectStore("products").clear();
      await tx.done;
    },
  };
}


function initApp() {
  const app = {
    db: null,
    activeMenu: 'pos',
    products: [],
    keyword: "",
    cart: [],
    isShowModalReceipt: false,
    isShowModalSuccess: false,
    isLoading: false,
    receiptNo: null,
    receiptDate: null,
    lpno: null,
    isMobiUser: false,
    car_present: false,
    model: null,
    video: null,
    socket: null,
    isManualLPnoModalOpen: false,
    isShowCameraChooseModal: false,
    lastLpno: '',
    lpnoCounter: 0,
    detectionStopped: false, 
    manualLpno: '',
    cameraDevices: [],

    initVideo() {
      this.video = document.getElementById('video');
      this.detectCameras();
    },

    openCameraChooseModal() {
      this.isShowCameraChooseModal = true;
    },

    closeCameraChooseModal() {
      this.isShowCameraChooseModal = false;
    },

    openLPnoModal() {
      this.isManualLPnoModalOpen = true;
    },

    closeLPnoModal() {
      this.isManualLPnoModalOpen = false;
      this.manualLpno = '';
    },

    async detectCameras() {
      try {
        const devices = await navigator.mediaDevices.enumerateDevices();
        console.log("Devices detected:", devices);
        this.cameraDevices = devices.filter(device => device.kind === 'videoinput');
        if (this.cameraDevices.length === 0) {
          console.error("No video input devices found.");
        }
      } catch (error) {
        console.error('Failed to detect cameras:', error);
      }
    },

    async selectCamera(deviceId) {
      try {
        console.log("Selected camera deviceId:", deviceId);
        const stream = await navigator.mediaDevices.getUserMedia({
          video: {
            deviceId: { exact: deviceId },
            width: { ideal: 1920 },
            height: { ideal: 1080 },
            frameRate: { ideal: 60 }
          }
        });
        this.video.srcObject = stream;
        this.video.play();
        this.closeCameraChooseModal();
      } catch (error) {
        console.error('Failed to select camera:', error);
      }
    },

    submitManualLpno() {
      if (this.manualLpno.trim() !== '') {
        this.lpno = this.manualLpno;
        this.isMobiUser = true;
      }
      this.closeLPnoModal();
    },


    async initDatabase() {
      this.db = await loadDatabase();
      await this.loadJsonData();
    },


    async loadJsonData() {
      await this.db.clearProducts();

      const response = await fetch("db.json");
      const data = await response.json();
      this.products = data.products;

      for (let product of data.products) {
        await this.db.addProduct(product);
      }
    },

    getProducts() {
      return this.products;
    },

    addToCart(product) {
      const index = this.findCartIndex(product);
      if (index === -1) {
        this.cart.push({
          productId: product.id,
          image: product.image,
          name: product.name,
          price: product.price,
          option: product.option,
          qty: 1,
        });
      } else {
        this.cart[index].qty += 1;
      }
      this.beep();
    },

    findCartIndex(product) {
      return this.cart.findIndex((p) => p.productId === product.id);
    },

    addQty(item, qty) {
      const index = this.cart.findIndex((i) => i.productId === item.productId);
      if (index === -1) {
        return;
      }
      const afterAdd = item.qty + qty;
      if (afterAdd === 0) {
        this.cart.splice(index, 1);
        this.clearSound();
      } else {
        this.cart[index].qty = afterAdd;
        this.beep();
      }
    },

    getItemsCount() {
      return this.cart.reduce((count, item) => count + item.qty, 0);
    },

    getTotalPrice() {
      return this.cart.reduce(
          (total, item) => total + item.qty * item.price,
          0
      );
    },

    submit() {
      const time = new Date();
      this.isShowModalReceipt = true;
      this.receiptNo = `MOBIPOS-${Math.round(time.getTime() / 1000)}`;
      this.receiptDate = this.dateFormat(time);
    },

    submitable() {
      return this.getTotalPrice() > 0;
    },

    closeModalReceipt() {
      this.isShowModalReceipt = false;
    },

    closeModalSuccess() {
      this.isShowModalSuccess = false;
    },

    resumeDetection() {
      this.detectionStopped = false;
      this.lastLpno = '',
      this.lpnoCounter= 0,
      document.querySelector('.flex-col.items-center.py-4').classList.remove('bg-blue-300');
    },

    dateFormat(date) {
      const formatter = new Intl.DateTimeFormat('id', { dateStyle: 'short', timeStyle: 'short'});
      return formatter.format(date);
    },

    numberFormat(number) {
      return (number || "")
          .toString()
          .replace(/^0|\./g, "")
          .replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1,");
    },

    priceFormat(number) {
      return number ? `${this.numberFormat(number)} 원` : `0 원`;
    },

    clear() {
      this.cart = [];
      this.receiptNo = null;
      this.receiptDate = null;
      this.isShowModalReceipt = false;
      this.isMobiUser = false;
      this.lpno = null;
      this.car_present = false;
      this.clearSound();
    },

    beep() {
      this.playSound("sound/beep.mp3");
    },

    clearSound() {
      this.playSound("sound/clear.mp3");
    },

    playSound(src) {
      const sound = new Audio();
      sound.src = src;
      sound.play();
      sound.onended = () => delete(sound);
    },

    joinWebRTCRoom(roomId) {
      const pcConfig = { iceServers: [{ urls: 'stun:stun.l.google.com:19302' }] };
      let localStream;
      let peerConnections = {};
      let mySocketId;
      
      const SOCKET_SERVER_URL = 'wss://anpr.mobipay.kr/';
      
      async function getLocalStream() {
          try {
              const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
              localStream = stream;
          } catch (error) {
              console.error('Error getting local stream:', error);
          }
      }
      
      const socket = io(SOCKET_SERVER_URL, {
          transports: ['websocket'],
          path: '/ws/'
      });

      getLocalStream();

      const callStatusElement = document.getElementById('call-status');
      callStatusElement.textContent = "음성 주문 대기중";
      callStatusElement.classList.remove('bg-gray-400');
      callStatusElement.classList.add('bg-lime-500'); 

      socket.on('connect', () => {
          console.log('Connected to WebRTC server');
          mySocketId = socket.id;
          socket.emit('join_room', { room: roomId });
      });

      socket.on('connect_error', (error) => {
          console.error('WebRTC Socket.IO connection error:', error);
      });

      socket.on('all_users', (users) => {
          console.log('All users in room:', users);
          users.forEach(userID => {
              if (userID !== mySocketId) {
                  createPeerConnection(userID);
              }
          });
      });

      socket.on('getOffer', async (data) => {
          console.log('Received offer from', data.offerSendID);
          const pc = createPeerConnection(data.offerSendID);
          await pc.setRemoteDescription(new RTCSessionDescription({ type: 'offer', sdp: data.sdp }));
          const answer = await pc.createAnswer();
          await pc.setLocalDescription(answer);
          socket.emit('answer', {
              sdp: answer.sdp,
              answerSendID: mySocketId,
              answerReceiveID: data.offerSendID
          });
      });

      socket.on('getAnswer', async (data) => {
          console.log('Received answer from', data.answerSendID);
          const pc = peerConnections[data.answerSendID];
          await pc.setRemoteDescription(new RTCSessionDescription({ type: 'answer', sdp: data.sdp }));
      });

      socket.on('getCandidate', async (data) => {
          console.log('Received candidate from', data.candidateSendID);
          const pc = peerConnections[data.candidateSendID];
          await pc.addIceCandidate(new RTCIceCandidate({
              sdpMid: data.sdpMid,
              sdpMLineIndex: data.sdpMLineIndex,
              candidate: data.candidate
          }));
      });

      socket.on('user_exit', (data) => {
          console.log('User exited:', data.id);
          if (peerConnections[data.id]) {
              peerConnections[data.id].close();
              delete peerConnections[data.id];
          }
      });

      function createPeerConnection(remoteSocketId) {
          if (peerConnections[remoteSocketId]) {
              return peerConnections[remoteSocketId];
          }

          const pc = new RTCPeerConnection(pcConfig);
          peerConnections[remoteSocketId] = pc;

          pc.onicecandidate = (event) => {
              if (event.candidate) {
                  socket.emit('candidate', {
                      candidate: event.candidate.candidate,
                      sdpMid: event.candidate.sdpMid,
                      sdpMLineIndex: event.candidate.sdpMLineIndex,
                      candidateSendID: mySocketId,
                      candidateReceiveID: remoteSocketId
                  });
              }
          };

          pc.ontrack = (event) => {
              console.log('Remote stream received');
              const remoteAudio = new Audio();
              remoteAudio.srcObject = event.streams[0];
              remoteAudio.play(); 
              callStatusElement.textContent = "음성 주문 연결됨";
              callStatusElement.classList.remove('bg-lime-500', 'bg-yellow-500');
              callStatusElement.classList.add('bg-green-500');
          };

          localStream.getTracks().forEach((track) => {
              pc.addTrack(track, localStream);
          });

          if (mySocketId < remoteSocketId) {
              createOffer(pc, remoteSocketId);
          }

          return pc;
      }

      async function createOffer(pc, remoteSocketId) {
          try {
              const offer = await pc.createOffer();
              await pc.setLocalDescription(offer);
              socket.emit('offer', {
                  sdp: offer.sdp,
                  offerSendID: mySocketId,
                  offerReceiveID: remoteSocketId
              });
          } catch (error) {
              console.error('Error creating offer:', error);
          }
      }
  },

    
    cancelLoading() {
      if (this.socket) {
        this.socket.close();
        this.socket = null;
      }
      this.isLoading = false;
    },

    requestPayMobi() {
      this.closeModalReceipt();
      this.isLoading = true;

      // websocket 연결
      //const socket = new WebSocket('wss://merchant.mobipay.kr/api/v1/merchants/websocket');
      this.socket = new WebSocket('wss://merchant.mobipay.kr/api/v1/merchants/websocket');

      let sessionId; // 세션 ID를 저장할 변수


      this.socket.onopen = async (event) => {
        console.log('WebSocket is open now.');

        let info = this.cart.map(item => `${item.name} x ${item.qty}`).join(', ');
        let paymentBalance = this.getTotalPrice();
        let carNumber = this.lpno || "번호 인식 실패";

        if(carNumber == null) {
          console.log("차량번호 없음");
           return;
        }

        // 결제 요청
        const paymentRequest = {
          "type": MERCHANT_TYPE, // 가맹점 종류
          "paymentBalance": paymentBalance,
          "carNumber": carNumber,
          "info": info // 결제 정보
        };

        try {
          const response = await postRequest('/merchants/payments/request', paymentRequest);
          console.log('결제 요청 성공, 결제 결과 대기 중...');
        } catch (error) {
          console.error('결제 요청 실패:', error);
          // 웹소켓 연결 해제
          //socket.close();
          this.socket.close();
          alert('결제 요청 실패');
        }
      };

      this.socket.onclose = (event) => {
        console.log('WebSocket is closed now.');
      };

      this.socket.onerror = (error) => {
        console.log('WebSocket error:', error);
      };

      this.socket.onmessage = (event) => {
        const message = JSON.parse(event.data);

        if (message.sessionId) {
          sessionId = message.sessionId;
          this.socket.send(JSON.stringify({ "type": MERCHANT_TYPE }));
        } else {
          if (message.success) {
            this.isLoading = false;
            this.isShowModalSuccess = true;
            this.lpno = null;
            this.isMobiUser = false; 
            this.cart = [];
            this.socket.close();
          } else {
            this.isLoading = false;
            alert('결제 취소');
          }
        }
      };
    },

    startCamera(facingMode) {
      return navigator.mediaDevices.getUserMedia({
        video: {
          facingMode: facingMode,
          width: { ideal: 1920 },
          height: { ideal: 1080 },
          frameRate: { ideal: 60 }
        }
      });
    },

    
    async detectObjects() {
      if (!this.detectionStopped){
        try {
          const predictions = await this.model.detect(this.video);
          this.car_present = false;

          predictions.forEach((prediction) => {
            if (prediction.class === 'car') {
              this.car_present = true;

              const [x, y, width, height] = prediction.bbox;

              if (height > 240 && width > 350) {
                const startTime = performance.now();
                const canvas = document.createElement('canvas');
                canvas.width = width;
                canvas.height = height;
                const context = canvas.getContext('2d');

                context.drawImage(this.video, x, y, width, height, 0, 0, width, height);

                const self = this;

                canvas.toBlob(async (blob) => {
                  const formData = new FormData();
                  formData.append('file', blob, 'image.jpg');
                  const endTime = performance.now();
                  const duration = endTime - startTime;
                  console.log("변환 시간: " + duration.toFixed(3) + "ms");

                  try {
                    const response = await fetch('https://anpr.mobipay.kr/predict/', {
                      method: 'POST',
                      body: formData,
                    });

                    const data = await response.json();
                    if (data !== null) {
                      const confidence = parseFloat(data.confidence);

                      if (confidence > 0.85) {
                        const detectedLpno = data.predicted_text;

                        if (self.lastLpno !== detectedLpno) {
                          self.lpnoCounter = 1;
                          self.lastLpno = detectedLpno;
                        } else {
                          self.lpnoCounter++;
                        }

                        if (self.lpnoCounter >= 3) {
                          self.detectionStopped = true;
                          self.$data.lpno = detectedLpno;
                          self.$data.isMobiUser = true;
                          const randomRoomId = Math.floor(Math.random() * 100000);
                          sendMenuList(MERCHANT_TYPE_URL, self.$data.lpno, randomRoomId);
                          console.log("Joining WebRTC room with ID:", randomRoomId);
                          self.joinWebRTCRoom(randomRoomId);


                          document.querySelector('.flex-col.items-center.py-4').classList.add('bg-blue-300');
                        } else {
                          self.$data.lpno = detectedLpno;
                          self.$data.isMobiUser = true;
                        }
                      } else {
                        console.log("정확도 낮음");
                        self.$data.lpno = null;
                        self.$data.isMobiUser = false;
                      }
                    }
                  } catch (error) {
                    console.error('POST 실패: ', error);
                  }
                });
              }
            }
          });

          if (!this.car_present) {
            // 차량 감지 안 된 경우 처리
          }

          setTimeout(() => {
            requestAnimationFrame(() => this.detectObjects());
          }, 600);
        } catch (error) {
          console.error('물체 감지 실패: ', error);

          setTimeout(() => {
            requestAnimationFrame(() => this.detectObjects());
          }, 600);
        }
      }else{
        // Detection 중지 상태면 2초 간격으로 함수 실행, 실제 detection 수행 안 함 (로컬 GPU 사용 X)
          setTimeout(() => {
            requestAnimationFrame(() => this.detectObjects());
          }, 2000);
      }
    },

    initANPR(){
      if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        this.startCamera({ exact: 'environment' })
            .then((stream) => {
              this.video.srcObject = stream;
              this.video.play();
              const track = stream.getVideoTracks()[0];
              const settings = track.getSettings();
              this.video.width = settings.width;
              this.video.height = settings.height;

              cocoSsd.load().then((loadedModel) => {
                this.model = loadedModel;
                this.detectObjects();
              }).catch((error) => {
                console.error('모델 로드 실패: ', error);
              });
            })
            .catch((error) => {
              console.warn('후면카메라 사용 불가하여 전면 카메라를 사용합니다.');
              this.startCamera('user')
                  .then((stream) => {
                    this.video.srcObject = stream;
                    this.video.play();
                    const track = stream.getVideoTracks()[0];
                    const settings = track.getSettings();
                    this.video.width = settings.width;
                    this.video.height = settings.height;

                    cocoSsd.load().then((loadedModel) => {
                      this.model = loadedModel;
                      this.detectObjects();
                    }).catch((error) => {
                      console.error('모델 로드 실패: ', error);
                    });
                  })
                  .catch((error) => {
                    console.error('전면카메라 사용 불가: ', error);
                  });
            });
      } else {
        console.error('getUserMedia 사용불가.');
      }
    },

    // 결제 취소
    isShowTransactionListModal: false,
    transactionList: [],

    openTransactionListModal() {
        this.isShowTransactionListModal = true;
    },

    closeTransactionListModal() {
        this.isShowTransactionListModal = false;
    },

    async getTransactions() {
        const url = `/api/v1/merchants/${MERCHANT_TYPE_URL}/transactions`;
        const data = await getRequest(url);
        this.transactionList = data?.items;
    },

    cancelTransaction(transactionUniqueNo) {
        const url = `/api/v1/merchants/${MERCHANT_TYPE_URL}/cancelled-transactions/${transactionUniqueNo}`;
        patchRequest(url).then(() => {
            this.getTransactions();
        });
    },
    // 결제 취소
  };

  async function sendMenuList(merchantType, carNumber, roomId) {
    const info = "아메리카노(HOT/ICE)#3800%카페라떼(HOT/ICE)#4500%카푸치노(HOT/ICE)#5100%쿠키#1600%마카롱#2400%크로와상#2200%잠봉뵈르#4400";
    
    const menuListRequest = {
      carNumber: carNumber,
      info: info,
      roomId: roomId
    };

    const apiUrl = `https://merchant.mobipay.kr/api/v1/merchants/${merchantType}/menu-list`;

    try {
      const response = await fetch(apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'merApikey': MER_API_KEY,
        },
        body: JSON.stringify(menuListRequest)
      });

      if (!response.ok) {
        throw new Error(`Menu list POST failed with status: ${response.status}`);
      }

      console.log('Menu list POST successful');
      return response;
    } catch (error) {
      console.error('Failed to send menu list:', error);
    }
  }

  async function postRequest(api, data = {}) {
    console.log(data);
    const response = await fetch(`${url}${api}`, {
      method: 'POST',
      mode: 'cors',
      cache: 'no-cache',
      headers: {
        'Content-Type': 'application/json',
        'merApikey': MER_API_KEY,
      },
      redirect: 'follow',
      referrerPolicy: 'no-referrer',
      body: JSON.stringify(data)
    });

    if(!response.ok){
      throw new Error(`postRequest() : error! status: ${response.status}`);
    }

    return response;
  }

  async function getRequest(url) {
      try {
          const response = await fetch(url, {
              method: 'GET',
              headers: {
                  'merApiKey': MER_API_KEY,
              },
          });

          if(response?.status !== 200) {
              alert('거래내역 조회에 실패했습니다.');
              console.error('Error:', response);
              return;
          }

          const data = await response.json();
          // console.log(data);
          return data

      } catch (error) {
          console.error('Error:', error);
          return;
      }
  }

  async function patchRequest(url) {
      try {
          const response = await fetch(url, {
              method: 'PATCH',
              headers: {
                  'merApiKey': MER_API_KEY,
              },
          });

          if(response?.status !== 200) {
              alert('거래 취소에 실패했습니다.');
              console.error('Error:', response);
              return;
          }

          alert('거래가 취소되었습니다.');

      } catch (error) {
          console.error('Error:', error);
          return;
      }
  }

  app.initANPR();
  return app;
}
