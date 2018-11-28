#include "pch.h"
#include "Message.h"

using namespace std;

Message::Message(string msg, Klient* sender){
	this->msg = msg;
	this->sender = sender;
	time_t now = std::time(0);
	tm *ltm = localtime(&now);
	this->date = to_string(ltm->tm_mday) + to_string(ltm->tm_mon) + to_string(ltm->tm_year);
	this->time = to_string(ltm->tm_hour) + to_string(ltm->tm_min);
}


Message::~Message()
{
}

string Message::toString1(int id_conv) {
	return this->sender->login + "|" + to_string(id_conv) + "|" + date + "|" + time + "|" + msg;
}

string Message::toString2() {
	return this->sender->login + "," + date + "," + time + "," + msg;
}
