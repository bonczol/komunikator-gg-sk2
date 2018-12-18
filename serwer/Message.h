#pragma once
#include <string>
#include <ctime>
#include <list>
#include "Klient.h"

class Message{
private:
	std::string msg;
	std::string date;
	std::string time;
	int id_conv;
	Klient* sender;

	std::string padTo(std::string str, const size_t num = 2, const char paddingChar = '0');

public:
	static std::map<Message*, Klient*> UNSENT_MSGS_BUFFER; 
	static pthread_mutex_t unsent_buffer_mutex;

	Message(std::string msg, Klient* sender, int id_conv);
	Message(){}
	std::string getMsg() { return this->msg; }
	std::string toString1();
	std::string toString2();
	static void push_to_unsent_buffer(Klient* k, Message* m);
	~Message();


	template<class Archive>
	void serialize(Archive & ar, const unsigned int version)
	{
		ar & msg;
		ar & date;
		ar & time;
		ar & id_conv;
		ar & sender;
	}
};

