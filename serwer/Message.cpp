#include "Message.h"

using namespace std;

std::map<Message*, Klient*> Message::UNSENT_MSGS_BUFFER; 
pthread_mutex_t Message::unsent_buffer_mutex = PTHREAD_MUTEX_INITIALIZER;

Message::Message(string msg, Klient* sender, int id_conv){
	this->msg = msg;
	this->sender = sender;
	time_t now = std::time(0);
	tm *ltm = localtime(&now);
	this->date = to_string(ltm->tm_mday) + to_string(ltm->tm_mon) + to_string(ltm->tm_year);
	this->time = to_string(ltm->tm_hour) + to_string(ltm->tm_min);
	this->id_conv = id_conv;
}

void Message::push_to_unsent_buffer(Klient* k, Message* m){
	pthread_mutex_lock(&Message::unsent_buffer_mutex);
	Message::UNSENT_MSGS_BUFFER[m] = k;
	pthread_mutex_unlock(&Message::unsent_buffer_mutex);
}


Message::~Message(){}

string Message::toString1() {
	return this->sender->login + "|" + to_string(this->id_conv) + "|" + date + "|" + time + "|" + msg;
}

string Message::toString2() {
	return this->sender->login + "," + date + "," + time + "," + msg;
}
