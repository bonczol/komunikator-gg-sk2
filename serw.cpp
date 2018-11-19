#include "pch.h"
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

#define SERVER_PORT 1337
#define QUEUE_SIZE 5

//struktura zawierająca dane, które zostaną przekazane do wątku
struct thread_data_t
{
	char buf[1000];
	int socket;
};

std::list<Klient*>* clients;

//funkcja opisującą zachowanie wątku - musi przyjmować argument typu (void *) i zwracać (void *)
void *ThreadBehavior1(void *t_data){
	pthread_detach(pthread_self());
	int socket = (int) t_data;
	Responder resp(socket, clients);
	resp.readAndRespond();
	close(socket);
	pthread_exit(NULL);
}


//funkcja obsługująca połączenie z nowym klientem
void handleConnection(int csd) {
	//wynik funkcji tworzącej wątek
	pthread_t thread;
	pthread_create(&thread, NULL, ThreadBehavior1, csd);
}



std::string join_list(std::list<std::string> list, std::string sep) {
	std::string result;
	for (std::string str : list) result = result + str + sep;
	return result.substr(0, result.length - 1);
}



void send_friends_list(Klient klient, int csd) {
	std::string result = "406|";
	for (Klient k : klient.friends) {
		result = result + k.nick + "," + k.login + "|";
	}
	result = result.substr(0, result.length - 1);
	write(csd, result, 1000);
	return;
}

 
//initialize registered clients at the start
void initialize_clients() {
	clients = new std::list<Klient*>();
	std::ifstream inFile;
	inFile.open("klienci.txt");
	if (!inFile) {
		std::cout << "Unable to open file datafile.txt";
		exit(1);   // call system to stop
	}
	std::string line;
	std::list<std::string> nicks;
	std::list<std::string> logins;
	std::list<std::string> passwords;
	std::list<std::pair<Klient*, std::list<std::string>> > friends_names_list;
	while (inFile >> line) {
		std::list<std::string> data = Responder::split_string(line, '|');
		std::string nick = data.front(); data.pop_front();
		std::string login = data.front(); data.pop_front();
		std::string password = data.front(); data.pop_front();
		Klient* k = new Klient(nick, login, password, false);
		clients -> push_back(k);
		friends_names_list.push_back(std::pair<Klient*, std::list<std::string>>
			(k, Responder::split_string(data.front(), ',')));
	}
	for (const auto& klient_friends_names : friends_names_list){
		Klient* k = klient_friends_names.first;
		for (const auto& friend_name : klient_friends_names.second){
			for (Klient* client : *clients) {
				if (client -> login == friend_name) k ->friends.push_back(client);
			}
		}
	}
	return;
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

	initialize_clients();
	while (1)
	{
		connection_socket_descriptor = accept(server_socket_descriptor, NULL, NULL);
		//accept_new_client(connection_socket_descriptor);
		printf("elo\n");
		handleConnection(connection_socket_descriptor);

	}

	close(server_socket_descriptor);
	return(0);
}
