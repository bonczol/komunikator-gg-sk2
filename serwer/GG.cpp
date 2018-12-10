#include <sys/types.h>
#include <sys/wait.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <netdb.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <pthread.h>
#include "Klient.h"
#include <list>
#include <string>
#include <iostream>
#include <fstream>
#include <utility>
#include "Responder.h"
#include "ServerSerializer.h"
#include <signal.h>

#define SERVER_PORT 1337
#define QUEUE_SIZE 5
bool WORK = true;

//struktura zawierająca dane, które zostaną przekazane do wątku
struct thread_data_t
{
	char buf[1000];
	int socket;
};


//funkcja opisującą zachowanie wątku - musi przyjmować argument typu (void *) i zwracać (void *)
void *ThreadBehavior1(void *t_data){
	pthread_detach(pthread_self());
	int* socket = (int*) t_data;
	Responder *resp = new Responder(*socket);
	resp->readAndRespond();
	delete resp;
	close(*socket);
	cout << "Connection lost on socket " << *socket << endl;
	pthread_exit(NULL);
}


//funkcja obsługująca połączenie z nowym klientem
void handleConnection(int csd) {
	//wynik funkcji tworzącej wątek
	pthread_t thread;
	pthread_create(&thread, NULL, ThreadBehavior1, &csd);
}
/*
void kill_handler(int signal){
	ServerSerializer s;
	s.serialize();
	return;
}*/


void* connection_accepter(void *server_socket){
	int* server_socket_descriptor = (int*) server_socket;
	while(1){
		int connection_socket_descriptor = accept(*server_socket_descriptor, NULL, NULL);
		cout << "New connection on socket " << connection_socket_descriptor << endl;
		handleConnection(connection_socket_descriptor);
	}
}

int main(int argc, char* argv[])
{
	int server_socket_descriptor;
	int connection_socket_descriptor;
	int bind_result;
	int listen_result;
	char reuse_addr_val = 1;
	struct sockaddr_in server_address;

	//inicjalizacja gniazda serwera
	memset(&server_address, 0, sizeof(struct sockaddr));
	server_address.sin_family = AF_INET;
	server_address.sin_addr.s_addr = htonl(INADDR_ANY);
	server_address.sin_port = htons(SERVER_PORT);

	server_socket_descriptor = socket(AF_INET, SOCK_STREAM, 0);
	if (server_socket_descriptor < 0)
	{
		fprintf(stderr, "%s: Błąd przy próbie utworzenia gniazda..\n", argv[0]);
		exit(1);
	}
	setsockopt(server_socket_descriptor, SOL_SOCKET, SO_REUSEADDR, (char*)&reuse_addr_val, sizeof(reuse_addr_val));

	bind_result = bind(server_socket_descriptor, (struct sockaddr*)&server_address, sizeof(struct sockaddr));
	if (bind_result < 0)
	{
		fprintf(stderr, "%s: Błąd przy próbie dowiązania adresu IP i numeru portu do gniazda.\n", argv[0]);
		exit(1);
	}

	listen_result = listen(server_socket_descriptor, 100);
	if (listen_result < 0) {
		fprintf(stderr, "%s: Błąd przy próbie ustawienia wielkości kolejki.\n", argv[0]);
		exit(1);
	}

/*
	struct sigaction sigIntHandler;
	sigIntHandler.sa_handler = kill_handler;
	sigemptyset(&sigIntHandler.sa_mask);
	sigIntHandler.sa_flags = 0;
	sigaction(SIGINT, &sigIntHandler, NULL);*/

	pthread_t thread;
	pthread_create(&thread, NULL, connection_accepter, &server_socket_descriptor);

	ServerSerializer s;
	std::ifstream pFile("serializedServer.txt", std::ifstream::in);
	bool empty = pFile.peek() == ifstream::traits_type::eof();
	cout << empty << endl;
	if(!empty) s.deserialize();
	cout << "Entering the loop\nTo stop the server press c+Enter\n";
	char c;
	while (WORK)
	{
		cin >> c;
		if(c == 'c'){
			s.serialize();
			WORK = false;
		}else if(c == 'k'){
			for (auto const& x : Klient::CLIENTS){
				std::cout << *x.second  << endl; 
			}
		}
		else if(c == 'd'){
			string login;
			cout << "login: ";
			cin >> login;
			pthread_mutex_lock(&Klient::clients_mutex);
			std::map<string, Klient*>::iterator klient_it = Klient::CLIENTS.find(login);
			if (klient_it != Klient::CLIENTS.end())
				Klient::CLIENTS.erase(login);
			pthread_mutex_unlock(&Klient::clients_mutex);
		}
		sleep(1);
	}
	close(server_socket_descriptor);
	return(0);
}
