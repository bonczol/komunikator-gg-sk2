#include <string>
#include <stdlib.h>
class Client{
public:
	string nick;
	string login;
	bool logged_in;
	string numer;
	int socket;
	Client(string a, string b, int c);
}

Client::Client(string a, string b, int c){
	nick = a;
	login = b;
	logged_in = true;
	socket = c;
}